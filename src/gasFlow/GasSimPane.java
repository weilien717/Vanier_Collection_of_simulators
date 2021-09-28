package gasflow;

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class GasSimPane extends AnchorPane {

    private AnimationTimer animationTimer = null;
    private double lastFrameTime = 0.0;

    private Boolean DevModeOn = false;

    private Boolean tutorialMode = true;

    private ArrayList<Volume> volumes = new ArrayList<>();
    private ArrayList<VolumeLink> volLinks = new ArrayList<>();
    private InfoPane infoPane;

    private Boolean settingVolume;                                              //used for onMousePressed and onMouseDragged to set volumes
    private Boolean settingVolumeLink;
    private Line tempLine = null;
    private Vector2D currentVolumeInitPos;                                      //used for onMousePressed and onMouseDragged to set volumes
    private double minSizeOfVolumes = 25;                                       //minimum x or y value that a volume can have in size

    public Label instructionLabel;

    public GasSimPane() {
        this.setOnKeyPressed(this::onKeyPressed);
        this.setOnKeyReleased(this::onKeyReleased);
        this.setOnMouseMoved(this::onMouseMoved);
        this.setOnMouseClicked(this::onMouseClicked);
        this.setOnMousePressed(this::onMousePressed);
        this.setOnMouseReleased(this::onMouseReleased);
        this.setOnMouseDragged(this::onMouseDragged);

        initialize();
    }

    public void addToPane(Node node) {
        this.getChildren().add(node);
    }

    public void removeFromPane(Node node) {
        this.getChildren().remove(node);
    }

    public Boolean intersect(Rectangle r1, Rectangle r2) {
        if (r1.intersects(r2.getX(), r2.getY(), r2.getWidth(), r2.getHeight())) {
            return true;
        }
        return false;
    }

    public Boolean intersect(double x, double y, Rectangle r1) {
        if (r1.contains(x, y)) {
            return true;
        }
        return false;
    }

    private void onKeyPressed(KeyEvent e) {
    }

    private void onKeyReleased(KeyEvent e) {
    }

    private void onMouseClicked(MouseEvent e) {
    }

    private void onMousePressed(MouseEvent e) {
        if (DevModeOn) {
            //Prints the information for debugging
            System.out.println("Pressed");
        }
        if (intersect(e.getX(), e.getY(), infoPane.getRect()) && this.getChildren().contains(infoPane.getRect())) {
            //ON CLICK OF INFO PANEL:

        } else {
            Boolean intersectsVolNode = false;
            Boolean intersectsVolume = false;
            for (Volume v : volumes) {
                for (VolNode n : v.getVolNodes()) {
                    if (intersect(e.getX(), e.getY(), n.getRect())) {
                        //ON CLICK OF VOLUME NODE:
                        intersectsVolNode = true;
                        settingVolumeLink = true;

                        n.setHasFocus(true);

                        try {
                            removeVolLink(n.getVolumeLink());
                        } catch (Exception e2) {
                            if (DevModeOn) {
                                System.out.println("Trying to access unexistant VolumeLink");
                            }
                        }

                        volLinks.add(new VolumeLink(n, this));
                        n.setVolumeLink(volLinks.get(volLinks.size() - 1));
                        addToPane(volLinks.get(volLinks.size() - 1).getLines()[0]);
                    }
                }
                if (intersect(e.getX(), e.getY(), v.getRect()) && !intersectsVolNode) {
                    //ON CLICK OF VOLUME:
                    intersectsVolume = true;
                    for (Node n : infoPane.getItems()) {
                        removeFromPane(n);
                    }
                    instructionLabel.setText("");
                    infoPane.setVolume(v);
                    showInfoPane();
                    //System.out.println("Change a single value and press ENTER to confirm");
                    for (Volume vol : volumes) {
                        vol.setFocus(false);
                    }
                    v.setFocus(true);
                }
            }
            if (!intersectsVolume && !intersectsVolNode) {
                //ON CLICK OF EMPTY SPACE:
                //begin creating new volume
                settingVolume = true;
                volumes.add(new Volume(new Vector2D(e.getX(), e.getY()), new Vector2D(0, 0), this));
                addToPane(volumes.get(volumes.size() - 1).getRect());
                addToPane(volumes.get(volumes.size() - 1).getGasColorRectangle());
                currentVolumeInitPos = volumes.get(volumes.size() - 1).getPosition();
                for (VolNode n : volumes.get(volumes.size() - 1).getVolNodes()) {
                    addToPane(n.getRect());
                }
            }
        }
    }

    private void onMouseMoved(MouseEvent e) {
    }

    private void onMouseReleased(MouseEvent e) {
        if (DevModeOn) {
            //Prints the information for debugging
            System.out.println("Released");
            if (settingVolume) {
                System.out.println("Position : ( " + volumes.get(volumes.size() - 1).getPosition().getX() + " , "
                        + volumes.get(volumes.size() - 1).getPosition().getY() + " )");
                System.out.println("Size : ( " + volumes.get(volumes.size() - 1).getSize().getX() + " , "
                        + volumes.get(volumes.size() - 1).getSize().getY() + " )");
            }
        }
        if (settingVolume) {
            //Checks to make sure Volumes aren't too small
            if (volumes.get(volumes.size() - 1).getSize().getX() < minSizeOfVolumes || volumes.get(volumes.size() - 1).getSize().getY() < minSizeOfVolumes) {
                removeVolume(volumes.get(volumes.size() - 1));
                System.out.println("Volumes must be larger than that.");

                //check for toggle volume links
                Boolean toggledAVolLink = false;
                for (VolumeLink l : volLinks) {
                    double distance = l.getDistance(e.getX(), e.getY());
                    if (DevModeOn) {
                        System.out.println("Distance is : " + distance);
                    }
                    if (distance < 7.5) {
                        l.toggleIsActivated();
                        toggledAVolLink = true;
                    }
                }
                if (!toggledAVolLink) {
                    instructionLabel.setText("Volumes must be larger than that.");

                    //removing infopane if it exists
                    hideInfoPane();
                }
            }
            //Checks to make sure Volumes don't overlap
            if (volumes.size() > 1) {
                for (int i = 0; i < volumes.size() - 1; i++) {
                    if (intersect(volumes.get(volumes.size() - 1).getRect(), volumes.get(i).getRect())) {
                        removeVolume(volumes.get(volumes.size() - 1));
                        System.out.println("Volumes mustn't overlap.");
                        instructionLabel.setText("Volumes mustn't overlap.");
                    }
                }
            }
            settingVolume = false;
        }
        if (settingVolumeLink) {
            boolean landsOnNode = false;
            VolNode nodeLandedOn = null;
            //checks to see if mouse lands on VolNode that isn't startNode
            for (Volume v : volumes) {
                for (VolNode n : v.getVolNodes()) {
                    if (intersect(e.getX(), e.getY(), n.getRect()) && !n.equals(volLinks.get(volLinks.size() - 1).getStartNode())) {
                        landsOnNode = true;
                        nodeLandedOn = n;
                    }
                }
            }
            //set all volnodes to not have focus
            for (Volume v : volumes) {
                for (VolNode n : v.getVolNodes()) {
                    if (n.getHasFocus()) {
                        n.setHasFocus(false);
                    }
                }
            }
            if (landsOnNode) {
                //set VolNode that has been landed on as EndNode of volume link
                volLinks.get(volLinks.size() - 1).setEndNode(nodeLandedOn);
                addToPane(volLinks.get(volLinks.size() - 1).getLines()[2]);
                nodeLandedOn.setVolumeLink(volLinks.get(volLinks.size() - 1));
                instructionLabel.setText("Click on a volume link to activate/deactivate it");
            } else {
                try {
                    removeVolLink(volLinks.get(volLinks.size() - 1));
                } catch (Exception e2) {
                    if (DevModeOn) {
                        System.out.println("WOW, I don't know how did this got called!");
                    }
                }
            }
        }
        tempLine = null;
        settingVolumeLink = false;
    }

    private void onMouseDragged(MouseEvent e) {
        if (settingVolume) {
            Vector2D mousePosition = new Vector2D(e.getX(), e.getY());
            if (!(mousePosition.getX() < 0) && !(mousePosition.getY() < 0) && !(mousePosition.getX() > 720) && !(mousePosition.getY() > 571)) {
                //TO DO : make this smooth ( if mouse pos < 0, volume pos = 0;
                volumes.get(volumes.size() - 1).setSize(mousePosition.sub(currentVolumeInitPos));
                if (mousePosition.getX() < currentVolumeInitPos.getX()) {
                    volumes.get(volumes.size() - 1).setPosition(new Vector2D(mousePosition.getX(), volumes.get(volumes.size() - 1).getPosition().getY()));
                }
                if (mousePosition.getY() < currentVolumeInitPos.getY()) {
                    volumes.get(volumes.size() - 1).setPosition(new Vector2D(volumes.get(volumes.size() - 1).getPosition().getX(), mousePosition.getY()));
                }
            }
        }
        if (settingVolumeLink) {
            Vector2D mousePosition = new Vector2D(e.getX(), e.getY());
            volLinks.get(volLinks.size() - 1).setEndPosition(mousePosition);
            if (tempLine != null) {
                removeFromPane(tempLine);
            }
            Line line = volLinks.get(volLinks.size() - 1).getLines()[1];
            addToPane(line);
            tempLine = line;
        }
    }

    public void removeVolume(Volume v) {
        removeFromPane(v.getRect());
        removeFromPane(v.getGasColorRectangle());
        for (VolNode n : v.getVolNodes()) {
            try {
                removeVolLink(n.getVolumeLink());
            } catch (Exception e) {
                if (DevModeOn) {
                    System.out.println("Trying to access unexistant VolumeLink");
                }
            }
            removeFromPane(n.getRect());
        }
        volumes.remove(v);
        if (volumes.isEmpty()) {
            tutorialMode = true;
            instructionLabel.setText("Click and Drag to Draw Volume...");
        }
    }

    public void removeVolLink(VolumeLink l) {
        volLinks.remove(l);
        l.deleteVolLinkLines();
    }

    public void hideInfoPane() {
        if (this.getChildren().contains(infoPane.getRect())) {
            removeFromPane(infoPane.getRect());
            for (Node n : infoPane.getItems()) {
                removeFromPane(n);
            }
            for (Volume vol : volumes) {
                vol.setFocus(false);
            }
        }
    }

    public void showInfoPane() {
        if (!(this.getChildren().contains(infoPane.getRect()))) {
            addToPane(infoPane.getRect());
            for (Node n : infoPane.getItems()) {
                addToPane(n);
            }
        } else {
            for (Node n : infoPane.getItems()) {
                addToPane(n);
            }
        }
    }

    public void updateInstructions() {
        if (this.getChildren().contains(instructionLabel)) {
            if (volumes.size() == 1 && tutorialMode) {
                if (!(this.getChildren().contains(infoPane.getRect()))) {
                    instructionLabel.setText("Click on Volume to bring up InfoPane");
                } else {
                    instructionLabel.setText("Change a single value and press ENTER to confirm");
                    tutorialMode = false;
                }
            }
        } else {
            if (volumes.isEmpty()) {
                tutorialMode = true;
                instructionLabel.setText("Click and Drag to Draw Volume...");
                addToPane(instructionLabel);
            }
        }
    }

    public void initialize() {

        lastFrameTime = 0.0f;
        long initialTime = System.nanoTime();

        AssetManager.preloadAllAssets();
        this.setBackground(AssetManager.getBackgroundImage());

        settingVolume = false;
        settingVolumeLink = false;

        instructionLabel = new Label("Click and Drag to Draw Volume...");
        addToPane(instructionLabel);

        // INIT all gameobjects
        for (Volume v : volumes) {
            addToPane(v.getRect());
            addToPane(v.getGasColorRectangle());
            for (VolNode n : v.getVolNodes()) {
                addToPane(n.getRect());
            }
        }

        infoPane = new InfoPane(new Vector2D(170, 561), this, null);

        if (animationTimer != null) {
            animationTimer.stop();
        }

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Time calculation                
                double currentTime = (now - initialTime) / 1000000000.0;
                double frameDeltaTime = currentTime - lastFrameTime;
                lastFrameTime = currentTime;

                // Game logic
                // Update positions/animations
                updateInstructions();

                for (Volume v : volumes) {
                    v.update(frameDeltaTime);
                    for (VolNode n : v.getVolNodes()) {
                        n.update(frameDeltaTime);
                    }
                }

                for (VolumeLink l : volLinks) {
                    l.update(frameDeltaTime);
                }

                infoPane.update(frameDeltaTime);
            }
        };
        animationTimer.start();
    }
}
