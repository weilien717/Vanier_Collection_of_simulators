package laser;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;

/**
 * Main Pane for Laser Simulation. Initializes elements & contains Animation timer.
 * @author Haytham Hnine 
 */

public class LaserSimPane extends AnchorPane {
    
    //Initializing Elements
    //Initializing objects & variables
    private double lastFrameTime = 0.0;

    private Laser pointer = null;
    private final Rotate rotatePointer = new Rotate();
    private PrismSquare square = null;

    private Beam reflectedBeam = null;
    private Beam beam = null;
    private Beam beam2 = null;
    private RefractedBeam rBeam = null;

    private final double environmentIndex = 1;

    private Boolean reflection = true;

    ObservableList<String> options
            = FXCollections.observableArrayList(
                    "Kerosene (1.4)",
                    "Glass (1.5)",
                    "Flint Glass (1.6)",
                    "Flint Glass, Impure (1.7)");

    ObservableList<String> colors
            = FXCollections.observableArrayList(
                    "Violet (380 nm)",
                    "Blue (450 nm)",
                    "Cyan (485 nm)",
                    "Green (500 nm)",
                    "Yellow (565 nm)",
                    "Orange (590 nm)",
                    "Red (700 nm)");
    
    //Initializing FXML objects
    Label objectBackgLabel;

    Line objectBackgLine;

    CheckBox reflectionBox;

    ComboBox wavelengthIORBox;

    Label wavelengthValueLabel;

    Label wavelengthColorLabel;

    Label tutorial;

    Label theta1L;

    Label theta2L;

    Label pIndexL;

    Label ratioL;

    Slider wavelengthSlider;

    ComboBox objectIORBox;

    Slider indexSlider;
    
    public LaserSimPane() {
        initialize();
        setStyle(("-fx-background-color: white;"));
        setStaticSceneElements(this);
        theta1L.setText("0.0");

    }

    public void addToPane(Node n) {
        this.getChildren().add(n);
    }

    public void removeFromPane(Node n) {
        this.getChildren().remove(n);
    }

    private void initialize() {
        setSpecialElements();
        theta2L.setText("0.0");
        lastFrameTime = 0.0f;
        long initialTime = System.nanoTime();
        
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Time calculation                
                double currentTime = (now - initialTime) / 1000000000.0;
                double frameDeltaTime = currentTime - lastFrameTime;
                lastFrameTime = currentTime;
                
                //Setting Text for the prism index label and the ratio of refraction n1/n2
                pIndexL.setText("" + square.getIndex());
                ratioL.setText("" + 1 / square.getIndex());
                
                //Updating the independent objects
                pointer.update(frameDeltaTime);
                square.update(frameDeltaTime);
                beam.update(frameDeltaTime, rotatePointer.getAngle(), (pointer.getRectangle().getY() + 17), 0);
                
                ////Corrections
                ///Position Correction
                //Checking if the prism has exited the visble pane, to then bring the prism back into the visible field.
                if(square.getRectangle().getX() <= 0) {
                    square.setPosition(new Vector2D(square.getPosition().getX() + 15, square.getPosition().getY()));
                }
                if(square.getRectangle().getY() <= 0) {
                    square.setPosition(new Vector2D(square.getPosition().getX(), square.getPosition().getY() + 15));
                }
                if(square.getRectangle().getX()+ square.getRectangle().getWidth() >= 720) {
                    square.setPosition(new Vector2D(square.getPosition().getX() - 15, square.getPosition().getY()));
                }
                if(square.getRectangle().getY() + square.getRectangle().getHeight() >= 600) {
                    square.setPosition(new Vector2D(square.getPosition().getX(), square.getPosition().getY() - 15));
                }
                
                //Checking if the prism overlaps of laser pointer, to move the prism away form the pointer. 
                Shape interP = Shape.intersect(pointer.getRectangle(), square.getRectangle());
                if (interP.getBoundsInLocal().getWidth() != -1) {
                    square.setPosition(new Vector2D(square.getPosition().getX() + 5, square.getPosition().getY()));
                }
                
                ///Beam/Prism Collision
                //Checking if the Beam has intersected with the Prism
                Shape intersect = Shape.intersect(beam.getLine(), square.getRectangle());
                

                if (intersect.getBoundsInLocal().getWidth() != -1 && (environmentIndex / square.getIndex()) != 1
                        && getBeamPrismIntersection().getX() != 0 && rotatePointer.getAngle() != 0) {
                    //Set Ends of first Beam and of first line of Refracted Beam
                    beam.setEnds(getBeamPrismIntersection());
                    rBeam.getLine().setStartX(getBeamPrismIntersection().getX());
                    rBeam.getLine().setStartY(getBeamPrismIntersection().getY());
                    
                   //Checking if the reflection box is checked, and updating the dotted reflection line.
                    if (!reflection && rotatePointer.getAngle() != 0) {
                        reflectedBeam.getLine().setVisible(true);
                        if (getBeamPrismIntersection().getX() == square.getRectangle().getX()) {
                            reflectedBeam.updateREFLECT(frameDeltaTime, rotatePointer.getAngle(), getBeamPrismIntersection().getY(), getBeamPrismIntersection().getX() - 25);
                        } else {
                            reflectedBeam.update(frameDeltaTime, -rotatePointer.getAngle(), getBeamPrismIntersection().getY(), getBeamPrismIntersection().getX() - 25);
                        }
                    } else {
                        reflectedBeam.getLine().setVisible(false);
                    }
                    
                    ///Refracted Beam
                    //Update Refracted Beam lines as necessary
                    rBeam.update(frameDeltaTime, rotatePointer.getAngle(),
                            getBeamPrismIntersection(), (environmentIndex / square.getIndex()),
                            square.getRectangle().getWidth(), square.getRectangle().getY(), square.getRectangle().getX());

                    if (rBeam.getLine2().getStartX() == 0) {
                        beam2.getLine().setStartX(rBeam.getLine().getEndX());
                        beam2.getLine().setStartY(rBeam.getLine().getEndY());

                        beam2.update(frameDeltaTime, rotatePointer.getAngle(), rBeam.getLine().getEndY(), rBeam.getLine().getEndX() - 25, 0);
                    } else if (rBeam.getLine2().getStartX() != 0 && rBeam.getLine2().getEndY() != square.getRectangle().getY()
                            && rBeam.getLine2().getEndY() != square.getRectangle().getY() + square.getRectangle().getHeight()) {
                        beam2.getLine().setStartX(rBeam.getLine().getEndX());
                        beam2.getLine().setStartY(rBeam.getLine().getEndY());

                        beam2.update(frameDeltaTime, -rotatePointer.getAngle(), rBeam.getLine2().getEndY(), rBeam.getLine2().getEndX() - 25, 0);
                    } else {
                        beam2.setStarts(0, 0);
                        beam2.setEnds(0, 0);
                    }
                    beam2.getLine().setStroke(beam.getLine().getStroke());
                    rBeam.getLine3().setStroke(beam.getLine().getStroke());
                    
                //Reset all lines after the intersecton of the Beam, as well as if getBeamPrismIntersection method returns 0
                } else if (getBeamPrismIntersection().getX() == 0) {
                    theta2L.setText("0.0");
                    rBeam.getLine().setStartX(0);
                    rBeam.getLine().setStartY(0);
                    rBeam.getLine().setEndX(0);
                    rBeam.getLine().setEndY(0);
                    removeFromPane(reflectedBeam.getLine());

                    rBeam.getLine2().setStartX(0);
                    rBeam.getLine2().setStartY(0);
                    rBeam.getLine2().setEndX(0);
                    rBeam.getLine2().setEndY(0);

                    rBeam.getLine3().setStartX(0);
                    rBeam.getLine3().setStartY(0);
                    rBeam.getLine3().setEndX(0);
                    rBeam.getLine3().setEndY(0);

                    beam2.getLine().setStartX(0);
                    beam2.getLine().setStartY(0);
                    beam2.getLine().setEndX(0);
                    beam2.getLine().setEndY(0);
                    reflection = true;
                    reflectionBox.setSelected(false);

                } else {
                    theta2L.setText("0.0");
                    rBeam.getLine().setStartX(0);
                    rBeam.getLine().setStartY(0);
                    rBeam.getLine().setEndX(0);
                    rBeam.getLine().setEndY(0);

                    rBeam.getLine2().setStartX(0);
                    rBeam.getLine2().setStartY(0);
                    rBeam.getLine2().setEndX(0);
                    rBeam.getLine2().setEndY(0);
                    removeFromPane(reflectedBeam.getLine());

                    rBeam.getLine3().setStartX(0);
                    rBeam.getLine3().setStartY(0);
                    rBeam.getLine3().setEndX(0);
                    rBeam.getLine3().setEndY(0);

                    beam2.getLine().setStartX(0);
                    beam2.getLine().setStartY(0);
                    beam2.getLine().setEndX(0);
                    beam2.getLine().setEndY(0);
                    reflection = true;
                    reflectionBox.setSelected(false);
                }

                theta2L.setText("" + rBeam.getRefrAngle());
            }
        }.start();
    }
    
    //Scan the Prism for the coordinates of the first point shared between the beam and the prism
    public Point2D getBeamPrismIntersection() {
        for (double i = square.getRectangle().getX(); i < (square.getRectangle().getX() + square.getRectangle().getWidth()); ++i) {
            for (double j = square.getRectangle().getY(); j < (square.getRectangle().getY() + square.getRectangle().getHeight()); ++j) {
                if (beam.getLine().contains(new Point2D(i, j)) && square.getRectangle().contains(new Point2D(i, j))) {
                    return new Point2D(i, j);
                }
            }
        }
        return new Point2D(0, 0);
    }
    
    //Setting elements which are subject to changes or updates
    private void setSpecialElements() {
        Line pointerRail = new Line();
        pointerRail.setLayoutX(0);
        pointerRail.setLayoutY(0);
        pointerRail.setStartX(25);
        pointerRail.setStartY(25);
        pointerRail.setEndX(25);
        pointerRail.setEndY(575);
        pointerRail.setStyle("-fx-fill: black");
        pointerRail.toBack();
        this.getChildren().add(pointerRail);

        theta1L = new Label();
        theta1L.setPrefSize(85, 25);
        theta1L.setLayoutX(610);
        theta1L.setLayoutY(260);
        theta1L.setStyle("-fx-border-color: black; -fx-background-color: white;");
        theta1L.setAlignment(Pos.CENTER);

        theta2L = new Label();
        theta2L.setPrefSize(85, 25);
        theta2L.setLayoutX(610);
        theta2L.setLayoutY(300);
        theta2L.setStyle("-fx-border-color: black; -fx-background-color: white;");
        theta2L.setAlignment(Pos.CENTER);

        pIndexL = new Label();
        pIndexL.setPrefSize(85, 25);
        pIndexL.setLayoutX(610);
        pIndexL.setLayoutY(377);
        pIndexL.setStyle("-fx-border-color: black; -fx-background-color: white;");
        pIndexL.setAlignment(Pos.CENTER);

        ratioL = new Label();
        ratioL.setPrefSize(85, 25);
        ratioL.setLayoutX(610);
        ratioL.setLayoutY(415);
        ratioL.setStyle("-fx-border-color: black; -fx-background-color: white;");
        ratioL.setAlignment(Pos.CENTER);

        wavelengthIORBox = new ComboBox();
        wavelengthIORBox.setPrefSize(150, USE_COMPUTED_SIZE);
        wavelengthIORBox.setLayoutX(519);
        wavelengthIORBox.setLayoutY(72);
        wavelengthIORBox.setItems(colors);

        wavelengthIORBox.setOnAction(e -> {
            if (wavelengthIORBox.getValue() == colors.get(0)) {
                pointer.getRectangle().setFill(Color.VIOLET);
                beam.getLine().setStyle("-fx-stroke: violet");

                beam2.setWavelength(380);
            }

            if (wavelengthIORBox.getValue() == colors.get(1)) {
                pointer.getRectangle().setFill(Color.BLUE);
                beam.getLine().setStyle("-fx-stroke: blue");

                beam2.setWavelength(450);
            }

            if (wavelengthIORBox.getValue() == colors.get(2)) {
                pointer.getRectangle().setFill(Color.CYAN);
                beam.getLine().setStyle("-fx-stroke: cyan");

                beam2.setWavelength(485);
            }

            if (wavelengthIORBox.getValue() == colors.get(3)) {
                pointer.getRectangle().setFill(Color.GREEN);
                beam.getLine().setStyle("-fx-stroke: green");

                beam2.setWavelength(500);
            }

            if (wavelengthIORBox.getValue() == colors.get(4)) {
                pointer.getRectangle().setFill(Color.YELLOW);
                beam.getLine().setStyle("-fx-stroke: yellow");

                beam2.setWavelength(565);
            }

            if (wavelengthIORBox.getValue() == colors.get(5)) {
                pointer.getRectangle().setFill(Color.ORANGE);
                beam.getLine().setStyle("-fx-stroke: orange");

                beam2.setWavelength(590);
            }

            if (wavelengthIORBox.getValue() == colors.get(6)) {
                pointer.getRectangle().setFill(Color.RED);
                beam.getLine().setStyle("-fx-stroke: red");

                beam2.setWavelength(700);
            }

            beam.getLine().setStrokeWidth(2.0f);
            reflectedBeam.getLine().setStroke(beam.getLine().getStroke());
        });

        objectIORBox = new ComboBox();
        objectIORBox.setPrefSize(150, USE_COMPUTED_SIZE);
        objectIORBox.setLayoutX(519);
        objectIORBox.setLayoutY(529);
        objectIORBox.setItems(options);

        objectIORBox.setOnAction(e -> {
            if (objectIORBox.getValue() == "Kerosene (1.4)") {
                square.getRectangle().setFill(Color.web("#ffa4a4"));
                square.setIndex(1.4);
                indexSlider.setValue(1.4);
            }
            if (objectIORBox.getValue() == "Glass (1.5)") {
                square.getRectangle().setFill(Color.web("#a3e0ff"));
                square.setIndex(1.5);

                indexSlider.setValue(1.5);
            }
            if (objectIORBox.getValue() == "Flint Glass (1.6)") {
                square.getRectangle().setFill(Color.web("#91f8ee"));
                square.setIndex(1.6);

                indexSlider.setValue(1.6);
            }
            if (objectIORBox.getValue() == "Flint Glass, Impure (1.7)") {
                square.getRectangle().setFill(Color.web("#6be1d5"));
                square.setIndex(1.7);

                indexSlider.setValue(1.7);
            }
            square.getRectangle().setStroke(Color.BLACK);
            square.getRectangle().setOpacity(0.7);
        });

        indexSlider = new Slider();
        indexSlider.setPrefSize(180, 25);
        indexSlider.setLayoutX(519);
        indexSlider.setLayoutY(554);
        indexSlider.setMin(1.4);
        indexSlider.setMax(1.7);

        indexSlider.setOnMouseClicked(e -> {
            Double n = indexSlider.getValue();

            objectIORBox.setValue("Custom");

            int tr = (int) (128 - ((n - 1.4) * (880 / 3)));
            int tg = (int) (208 - ((n - 1.4) * (1430 / 3)));
            int tb = (int) (255 - ((n - 1.4) * (1750 / 3)));
            String webColor = "rgb(" + tr + "," + tg + "," + tb + ")";
            square.getRectangle().setFill(Color.web(webColor));
            square.getRectangle().setStroke(Color.BLACK);
            square.getRectangle().setOpacity(0.7);
            square.setIndex(n);
        });

        square = new PrismSquare(new Vector2D(404, 481), 95);
        square.getRectangle().setFill(Color.GRAY);
        square.getRectangle().setOpacity(0.7);

        square.getRectangle().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                        objectBackgLabel.setPrefSize(195, 115);
                        objectBackgLabel.setLayoutX(511);
                        objectBackgLabel.setLayoutY(471);
                        if (contains(objectBackgLine)) {
                            removeFromPane(objectBackgLine);
                        }

                        if (contains(tutorial)) {
                            removeFromPane(tutorial);
                        }

                        square.setPosition(new Vector2D(e.getX() - square.getRectangle().getWidth() / 2, e.getY() - square.getRectangle().getHeight() / 2));
                }
            }
        });

        this.getChildren().add(square.getRectangle());

        beam = new Beam(new Vector2D(0, 0));
        beam.setEnds(0, 0);
        this.getChildren().add(beam.getLine());

        beam2 = new Beam(new Vector2D(0, 0));
        beam.setEnds(0, 0);
        this.getChildren().add(beam2.getLine());

        rBeam = new RefractedBeam((new Vector2D(50, 50)));
        rBeam.setEnds(751, 651);
        addToPane(rBeam.getLine3());
        this.getChildren().add(rBeam.getLine());
        this.getChildren().add(rBeam.getLine2());

        reflectedBeam = new Beam(new Vector2D(beam.getLine().getEndX(), beam.getLine().getEndX()));
        reflectedBeam.setEnds(0, 0);
        reflectedBeam.getLine().setStroke(beam.getLine().getStroke());
        reflectedBeam.getLine().getStrokeDashArray().addAll(20d, 10d);
        this.getChildren().add(reflectedBeam.getLine());

        pointer = new Laser();
        pointer.getRectangle().setFill(Color.PINK);
        pointer.getRectangle().setStroke(Color.BLACK);
        pointer.getRectangle().getTransforms().add(rotatePointer);

        pointer.getRectangle().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    if (e.getY() > 25 && e.getY() < 575) {
                        pointer.setPosition(new Vector2D(8, e.getY() - 17));
                    }
                    beam.setPosition(new Vector2D(pointer.getPosition().getX() + 84, pointer.getPosition().getY() + 17));

                    if (contains(tutorial)) {
                        removeFromPane(tutorial);
                    }

                    rotatePointer.setAngle(0);
                }
            }
        });

        pointer.getRectangle().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                rotatePointer.setPivotX(25);
                rotatePointer.setPivotY(pointer.getRectangle().getY() + 17);
                if (e.getButton().equals(MouseButton.SECONDARY)
                        && e.getY() < rotatePointer.getPivotY()) {
                    if (pointer.getRectangle().getY() > 15
                            || rotatePointer.getAngle() > 0) {
                        if (rotatePointer.getAngle() > -75) {
                            rotatePointer.setAngle(rotatePointer.getAngle() - 15.0);
                        }
                    }
                }

                if (e.getButton().equals(MouseButton.SECONDARY)
                        && e.getY() > rotatePointer.getPivotY()
                        && pointer.getRectangle().getY() < 558) {
                    if (pointer.getRectangle().getY() < 557
                            || rotatePointer.getAngle() < 0) {
                        if (rotatePointer.getAngle() < 75) {
                            rotatePointer.setAngle(rotatePointer.getAngle() + 15.0);
                        }
                    }
                }

                theta1L.setText("" + -rotatePointer.getAngle());
            }
        });

    }
    
    public Node[] getSpecialElements() {
        Node[] specialElementArray = {wavelengthIORBox, objectIORBox, theta1L, theta2L, pIndexL, ratioL,
            indexSlider};
        return specialElementArray;
    }

    public Laser getLaser() {
        return pointer;
    }

    public PrismSquare getSquare() {
        return square;
    }

    public Boolean contains(Node n) {
        return this.getChildren().contains(n);
    }
    
    //Setting elements which are more likely to stay constant, and not change.
    private void setStaticSceneElements(LaserSimPane root) {
        objectBackgLabel = new Label();
        objectBackgLabel.setLayoutX(391);
        objectBackgLabel.setLayoutY(471);
        objectBackgLabel.setPrefSize(320, 115);
        objectBackgLabel.setStyle("-fx-border-color: black; -fx-background-color: #efefef;");
        objectBackgLabel.toFront();
        root.getChildren().add(objectBackgLabel);

        tutorial = new Label();
        tutorial.setLayoutX(30);
        tutorial.setLayoutY(14);
        tutorial.setPrefSize(427, 54);
        tutorial.setText("Right click on laser pointer to change its angle . \n           Drag and Drop Prism and observe the refraction.");
        tutorial.setAlignment(Pos.CENTER);
        tutorial.toBack();
        root.getChildren().add(tutorial);

        Label dataBackgLabel = new Label();
        dataBackgLabel.setLayoutX(511);
        dataBackgLabel.setLayoutY(200);
        dataBackgLabel.setPrefSize(195, 257);
        dataBackgLabel.setStyle("-fx-border-color: black; -fx-background-color: #efefef;");
        dataBackgLabel.toFront();
        root.getChildren().add(dataBackgLabel);

        Label environmentBackgLabel = new Label();
        environmentBackgLabel.setLayoutX(511);
        environmentBackgLabel.setLayoutY(14);
        environmentBackgLabel.setPrefSize(195, 95);
        environmentBackgLabel.setStyle("-fx-border-color: black; -fx-background-color: #efefef;");
        root.getChildren().add(environmentBackgLabel);

        Label wavelengthBackgLabel = new Label();
        wavelengthBackgLabel.setPrefSize(195, 70);
        wavelengthBackgLabel.setLayoutX(511);
        wavelengthBackgLabel.setLayoutY(117);
        wavelengthBackgLabel.setStyle("-fx-border-color: black; -fx-background-color: #efefef;");
        root.getChildren().add(wavelengthBackgLabel);

        Label dataTitleLabel = new Label();
        dataTitleLabel.setPrefSize(105, 25);
        dataTitleLabel.setLayoutX(519);
        dataTitleLabel.setLayoutY(209);
        dataTitleLabel.setStyle("-fx-font-weight: bold");
        dataTitleLabel.setFont(new Font(14.0));
        dataTitleLabel.setText("Data");
        root.getChildren().add(dataTitleLabel);

        Label ddTT = new Label();
        ddTT.setPrefSize(180, 17);
        ddTT.setLayoutX(519);
        ddTT.setLayoutY(234);
        ddTT.setText("Angles, Refraction Index");
        root.getChildren().add(ddTT);

        Label theta1 = new Label();
        theta1.setPrefSize(85, 25);
        theta1.setLayoutX(519);
        theta1.setLayoutY(260);
        theta1.setText("Incidence°");
        root.getChildren().add(theta1);

        Label theta2 = new Label();
        theta2.setPrefSize(85, 25);
        theta2.setLayoutX(519);
        theta2.setLayoutY(300);
        theta2.setText("Refraction°");
        root.getChildren().add(theta2);

        Label ei = new Label();
        ei.setPrefSize(180, 25);
        ei.setLayoutX(519);
        ei.setLayoutY(340);
        ei.setText("Env. Index              1.0029(Air)");
        root.getChildren().add(ei);

        Label pi = new Label();
        pi.setPrefSize(85, 25);
        pi.setLayoutX(519);
        pi.setLayoutY(377);
        pi.setText("Prism Index");
        root.getChildren().add(pi);

        Label ratio = new Label();
        ratio.setPrefSize(85, 25);
        ratio.setLayoutX(519);
        ratio.setLayoutY(415);
        ratio.setText("Ratio");
        root.getChildren().add(ratio);

        Label rrTitleLabel = new Label();
        rrTitleLabel.setPrefSize(105, 25);
        rrTitleLabel.setLayoutX(519);
        rrTitleLabel.setLayoutY(127);
        rrTitleLabel.setStyle("-fx-font-weight: bold");
        rrTitleLabel.setFont(new Font(14.0));
        rrTitleLabel.setText("Reflected Ray");
        root.getChildren().add(rrTitleLabel);

        Label WavelengthTitleLabel = new Label();
        WavelengthTitleLabel.setPrefSize(105, 25);
        WavelengthTitleLabel.setLayoutX(519);
        WavelengthTitleLabel.setLayoutY(23);
        WavelengthTitleLabel.setStyle("-fx-font-weight: bold");
        WavelengthTitleLabel.setFont(new Font(14.0));
        WavelengthTitleLabel.setText("Wavelength");
        root.getChildren().add(WavelengthTitleLabel);

        Label wavelengthDCVLabel = new Label();
        wavelengthDCVLabel.setPrefSize(180, 17);
        wavelengthDCVLabel.setLayoutX(519);
        wavelengthDCVLabel.setLayoutY(48);
        wavelengthDCVLabel.setText("Discrete Color Values");
        root.getChildren().add(wavelengthDCVLabel);

        Label objectTitleLabel = new Label();
        objectTitleLabel.setPrefSize(105, 25);
        objectTitleLabel.setLayoutX(520);
        objectTitleLabel.setLayoutY(481);
        objectTitleLabel.setStyle("-fx-font-weight: bold");
        objectTitleLabel.setFont(new Font(14.0));
        objectTitleLabel.setText("Prism");
        root.getChildren().add(objectTitleLabel);

        Label objectIORLabel = new Label();
        objectIORLabel.setPrefSize(180, 17);
        objectIORLabel.setLayoutX(520);
        objectIORLabel.setLayoutY(506);
        objectIORLabel.setText("Index of Refraction (n)");
        root.getChildren().add(objectIORLabel);

        objectBackgLine = new Line();
        objectBackgLine.setLayoutX(511);
        objectBackgLine.setLayoutY(481);
        objectBackgLine.setStartY(95);
        objectBackgLine.setStyle("-fx-fill: black");
        root.getChildren().add(objectBackgLine);

        reflectionBox = new CheckBox();
        reflectionBox.setLayoutX(519);
        reflectionBox.setLayoutY(157);
        reflectionBox.setPrefSize(111, 17);
        reflectionBox.setText("Show Reflection");
        reflectionBox.toFront();
        reflectionBox.setOnAction(e -> {
            reflection = !reflection;

            if (contains(reflectedBeam.getLine())) {
                removeFromPane(reflectedBeam.getLine());
            } else {
                addToPane(reflectedBeam.getLine());
            }
        });
        root.getChildren().add(reflectionBox);

        //add special elements
        for (int i = 0; i < root.getSpecialElements().length; ++i) {
            root.getChildren().add(root.getSpecialElements()[i]);
        }

        root.getChildren().add(root.getLaser().getRectangle());
    }
}
