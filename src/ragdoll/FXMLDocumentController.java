package ragdoll;

/**
 * @author Jonathan Hubermann
 * @Subject Integrative Project 420-204-RE
 * @Project Ragdoll Simulation
 */

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

// FXMLDocumentController class controls the FXMLDocumentRagdoll.fxml file and holds the AnimationTimer loop
public class FXMLDocumentController implements Initializable {

    private static final float timeStep = 0.02f;    // time step at which to update all entities
    protected final double STIFFNESS = -0.35;   // constant for spring stiffness
    protected final double BUS_ACCELERATION = 0.6;  // constant bus acceleration
    protected final double BUS_LEFT_BOUND = 33.0;   // bounds for bus walls
    protected final double BUS_TOP_BOUND = 95.0;
    protected final double BUS_RIGHT_BOUND = 569.0;
    protected final double BUS_BOTTOM_BOUND = 410.0;

    protected ArrayList<SimulationEntity> simList = new ArrayList<>();
    protected ArrayList<RectangleEntity> rCount = new ArrayList<>();
    protected ArrayList<CircleEntity> cCount = new ArrayList<>();
    protected ArrayList<SpringEntity> sCount = new ArrayList<>();
    protected Map<SimulationEntity, MenuItem> menuItemOfSimEnt = new LinkedHashMap<>();
    protected MultiMap<SimulationEntity, MenuItem> menuItemsLinkedToSimEnt = new MultiMap<>();
    protected MultiMap<SimulationEntity, SpringEntity> springEntityLinkedToSimEnt = new MultiMap<>();

    //  adding all FXML containers and controls to controller
    @FXML
    AnchorPane pane;
    @FXML
    Canvas canvas;
    @FXML
    Button pauseButton;
    @FXML
    TabPane controlPane;
    @FXML
    Tab environmentTab;
    @FXML
    Tab entityTab;
    @FXML
    AnchorPane environmentTabPane;
    @FXML
    Label targetVelocityLabel;
    @FXML
    Spinner<Double> targetVelocitySpinner;
    @FXML
    Label targetVelocityUnitsLabel;
    @FXML
    AnchorPane entityTabPane;
    @FXML
    MenuButton addObjectMenuButton;
    @FXML
    MenuItem rectangleMenuItem;
    @FXML
    MenuItem circleMenuItem;
    @FXML
    MenuItem springMenuItem;
    @FXML
    Label addObjectLabel;
    @FXML
    Label removeObjectLabel;
    @FXML
    MenuButton removeObjectMenuButton;
    @FXML
    Label rectangleWidthLabel;
    @FXML
    Label rectangleHeightLabel;
    @FXML
    Label rectangleMassLabel;
    @FXML
    Spinner<Double> rectangleWidthSpinner;
    @FXML
    Spinner<Double> rectangleHeightSpinner;
    @FXML
    Spinner<Double> rectangleMassSpinner;
    @FXML
    Button rectangleAddButton;
    @FXML
    Label circleRadiusLabel;
    @FXML
    Spinner<Double> circleRadiusSpinner;
    @FXML
    Label circleMassLabel;
    @FXML
    Spinner<Double> circleMassSpinner;
    @FXML
    Button circleAddButton;
    @FXML
    Label objectSelectPositionLabel;
    @FXML
    Label objectPositionLabel;
    @FXML
    Label objectPositionXLabel;
    @FXML
    Label objectPositionYLabel;
    @FXML
    Label springSelectObjectLabel;
    @FXML
    ComboBox<SimulationEntity> springSelectObjectComboBox;
    @FXML
    Label springRectangleCornerLabel;
    @FXML
    ComboBox<Vector2D> springRectangleCornerComboBox;
    @FXML
    Button springAddButton;
    @FXML
    Label environmentCollisionToggleLabel;
    @FXML
    ToggleButton environmentCollisionToggleButton;
    @FXML
    Label rectangleCollisionToggleLabel;
    @FXML
    Label rectangleCollisionBetaToggleLabel;
    @FXML
    ToggleButton rectangleCollisionToggleButton;

    private long previousTime = 0;
    private float accumulatedTime = 0;
    private double mouseX;
    private double mouseY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // creation of JavaFX groups for faster and easier loading of JavaFX elements
        Group entityTabPaneDisplay = new Group(addObjectLabel, addObjectMenuButton, removeObjectLabel, removeObjectMenuButton);

        Group rectangleMenuItemDisplay = new Group(rectangleWidthLabel, rectangleWidthSpinner, rectangleHeightLabel, rectangleHeightSpinner,
                rectangleMassLabel, rectangleMassSpinner, rectangleAddButton);

        Group circleMenuItemDisplay = new Group(circleRadiusLabel, circleRadiusSpinner, circleMassLabel, circleMassSpinner, circleAddButton);

        Group springMenuItemDisplay = new Group(springSelectObjectLabel, springSelectObjectComboBox, springRectangleCornerLabel, springRectangleCornerComboBox, springAddButton);

        Group objectSelectPositionDisplay = new Group(objectSelectPositionLabel, objectPositionLabel, objectPositionXLabel, objectPositionYLabel);

        entityTabPane.getChildren().addAll(entityTabPaneDisplay, rectangleMenuItemDisplay, circleMenuItemDisplay, springMenuItemDisplay, objectSelectPositionDisplay);

        targetVelocitySpinner.setValueFactory(new DoubleSpinnerValueFactory(0, 30, 0));
        rectangleWidthSpinner.setValueFactory(new DoubleSpinnerValueFactory(20, 250, 100));
        rectangleHeightSpinner.setValueFactory(new DoubleSpinnerValueFactory(20, 150, 50));
        rectangleMassSpinner.setValueFactory(new DoubleSpinnerValueFactory(1, 50, 1));
        circleRadiusSpinner.setValueFactory(new DoubleSpinnerValueFactory(20, 100, 30));
        circleMassSpinner.setValueFactory(new DoubleSpinnerValueFactory(1, 50, 1));

        environmentCollisionToggleButton.setSelected(false);
        rectangleCollisionToggleButton.setSelected(false);

        // StringConverter to set ComboBox menuitems text from the SimulationEntity object each represents
        springSelectObjectComboBox.setConverter(new StringConverter<SimulationEntity>() {
            @Override
            public String toString(SimulationEntity object) {
                if (object instanceof RectangleEntity) {
                    return "Rectangle " + (rCount.indexOf(object) + 1);
                } else {
                    return "Circle " + (cCount.indexOf(object) + 1);
                }
            }
            @Override
            public SimulationEntity fromString(String string) {
                if (string.toLowerCase().contains("Rectangle".toLowerCase())) {
                    string = string.replace("Rectangle ", "");
                    int i = Integer.valueOf(string);
                    return rCount.get(i - 1);
                } else if (string.toLowerCase().contains("Circle".toLowerCase())) {
                    string = string.replace("Circle ", "");
                    int i = Integer.valueOf(string);
                    return cCount.get(i - 1);
                } else {
                    return null;
                }
            }
        });

        // StringConverter to set ComboBox menuitems text from the Vector2D object each represents
        springRectangleCornerComboBox.setConverter(new StringConverter<Vector2D>() {
            @Override
            public String toString(Vector2D object) {
                RectangleEntity re = (RectangleEntity) springSelectObjectComboBox.getValue();
                if (object.equal(re.topLeft)) {
                    return "Top Left";
                } else if (object.equal(re.topRight)) {
                    return "Top Right";
                } else if (object.equal(re.bottomRight)) {
                    return "Bottom Right";
                } else if (object.equal(re.bottomLeft)) {
                    return "Bottom Left";
                } else {
                    return null;
                }
            }
            @Override
            public Vector2D fromString(String string) {
                return null;
            }
        });

        controlPane.setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        //  Functionality for dragging the controlPane
        controlPane.setOnMouseDragged(e -> {
            double deltaX = e.getSceneX() - mouseX;
            double deltaY = e.getSceneY() - mouseY;
            controlPane.relocate(controlPane.getLayoutX() + deltaX, controlPane.getLayoutY() + deltaY);
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        // toggle functionality for ToggleButtons' text
        environmentCollisionToggleButton.setOnAction(e -> {
            if (environmentCollisionToggleButton.getText().equals("ON")) {
                environmentCollisionToggleButton.setText("OFF");
            } else {
                environmentCollisionToggleButton.setText("ON");
            }
        });

        // toggle functionality for ToggleButtons' text
        rectangleCollisionToggleButton.setOnAction(e -> {
            if (rectangleCollisionToggleButton.getText().equals("ON")) {
                rectangleCollisionToggleButton.setText("OFF");
            } else {
                rectangleCollisionToggleButton.setText("ON");
            }
        });

        // resets Panes with correct Labels and Controls when tab selection changes
        entityTab.setOnSelectionChanged(e -> {
            for (Node n : rectangleMenuItemDisplay.getChildren()) {
                n.setVisible(false);
            }
            for (Node n : circleMenuItemDisplay.getChildren()) {
                n.setVisible(false);
            }
            for (Node n : springMenuItemDisplay.getChildren()) {
                n.setVisible(false);
            }
            for (Node n : objectSelectPositionDisplay.getChildren()) {
                n.setVisible(false);
            }
            for (Node n : entityTabPaneDisplay.getChildren()) {
                n.setVisible(true);
            }
            objectSelectPositionLabel.setText("Click on screen to select spawn point!");
            objectPositionXLabel.setText("");
            objectPositionYLabel.setText("");
            springSelectObjectComboBox.getSelectionModel().clearSelection();
            springRectangleCornerComboBox.getItems().clear();
        });

        rectangleMenuItem.setOnAction(e -> {
            for (Node n : entityTabPaneDisplay.getChildren()) {
                n.setVisible(false);
            }
            for (Node n : rectangleMenuItemDisplay.getChildren()) {
                n.setVisible(true);
            }
            for (Node n : objectSelectPositionDisplay.getChildren()) {
                n.setVisible(true);
            }
            pane.setOnMouseClicked(o -> {
                objectPositionXLabel.setText(Double.toString(o.getSceneX()));
                objectPositionYLabel.setText(Double.toString(o.getSceneY()));
            });
        });

        circleMenuItem.setOnAction(e -> {
            for (Node n : entityTabPaneDisplay.getChildren()) {
                n.setVisible(false);
            }
            for (Node n : circleMenuItemDisplay.getChildren()) {
                n.setVisible(true);
            }
            for (Node n : objectSelectPositionDisplay.getChildren()) {
                n.setVisible(true);
            }
            pane.setOnMouseClicked(o -> {
                objectPositionXLabel.setText(Double.toString(o.getSceneX()));
                objectPositionYLabel.setText(Double.toString(o.getSceneY()));
            });
        });

        springMenuItem.setOnAction(e -> {
            for (Node n : entityTabPaneDisplay.getChildren()) {
                n.setVisible(false);
            }
            for (Node n : springMenuItemDisplay.getChildren()) {
                n.setVisible(true);
            }
            for (Node n : objectSelectPositionDisplay.getChildren()) {
                n.setVisible(true);
            }
            springRectangleCornerLabel.setVisible(false);
            springRectangleCornerComboBox.setVisible(false);
            objectSelectPositionLabel.setText("Click on screen to select origin point!");
            pane.setOnMouseClicked(o -> {
                objectPositionXLabel.setText(Double.toString(o.getSceneX()));
                objectPositionYLabel.setText(Double.toString(o.getSceneY()));
            });
        });

        // handles event for adding a new RectangleEntity to the simulation
        rectangleAddButton.setOnAction(e -> {
            try { // ensures that rectangle is in bounds of bus
                if (Double.parseDouble(objectPositionXLabel.getText()) < BUS_LEFT_BOUND || (Double.parseDouble(objectPositionXLabel.getText()) + rectangleWidthSpinner.getValue()) > BUS_RIGHT_BOUND || Double.parseDouble(objectPositionYLabel.getText()) < BUS_TOP_BOUND || (Double.parseDouble(objectPositionYLabel.getText()) + rectangleHeightSpinner.getValue()) > BUS_BOTTOM_BOUND) {
                    throw new Exception();
                }
                // creates the RectangleEntity
                RectangleEntity r = new RectangleEntity(Double.parseDouble(objectPositionXLabel.getText()), Double.parseDouble(objectPositionYLabel.getText()), rectangleWidthSpinner.getValue(), rectangleHeightSpinner.getValue(), rectangleMassSpinner.getValue());
                simList.add(r);
                rCount.add(r);

                Force gravity = new Force(r.center, x -> new Vector2D(0, r.mass * 9.81));
                r.extForces.add(gravity);

                MenuItem m = new MenuItem("Rectangle " + (rCount.indexOf(r) + 1));

                // handles events when a RectangleEntity is removed
                m.setOnAction(event -> {
                    try {
                        simList.removeAll(springEntityLinkedToSimEnt.remove(r)); // removes all springs associated to the rectangle
                        removeObjectMenuButton.getItems().removeAll(menuItemsLinkedToSimEnt.remove(r)); // removes all menuItems associated to those springs
                    } catch (NullPointerException n) {}
                    simList.remove(r);  // removes rectangle from list of SimulationEntities
                    removeObjectMenuButton.getItems().remove(m);    // removes the "remove" menuItem of the rectangle
                    springSelectObjectComboBox.getItems().remove(r);    // removes the menuItem of the rectangle from add SpringEntity display
                    menuItemOfSimEnt.remove(r);
                    if (pauseButton.getText().equals("Resume")) {   // draws the new RectangleEntity even if game is paused
                        for (SimulationEntity se : simList) {
                            canvas.getGraphicsContext2D().clearRect(0, 0, pane.getWidth(), pane.getHeight());
                            se.draw(canvas.getGraphicsContext2D());
                        }
                    }
                });
                // adds the new RectangleEntity to all lists that track it and any other objects associated to it
                removeObjectMenuButton.getItems().add(m);
                springSelectObjectComboBox.getItems().add(r);
                menuItemOfSimEnt.put(r, m);

                if (pauseButton.getText().equals("Resume")) {
                    r.draw(canvas.getGraphicsContext2D());
                }
            } catch (NumberFormatException ex) { // handles creation error
                Alert alert = new Alert(Alert.AlertType.ERROR, "Select an initial spawn position for the rectangle!");
                alert.setTitle("ERROR!");
                alert.showAndWait();
            } catch (Exception ex) { // handles creation error
                Alert alert = new Alert(Alert.AlertType.ERROR, "Select an initial spawn position for the rectangle within the school bus!");
                alert.setTitle("ERROR!");
                alert.showAndWait();
            }
        });

        // handles event for adding a new CircleEntity to the simulation
        circleAddButton.setOnAction(e -> {
            try { // ensures that circle is in bounds of bus
                if ((Double.parseDouble(objectPositionXLabel.getText()) - circleRadiusSpinner.getValue()) < BUS_LEFT_BOUND || (Double.parseDouble(objectPositionXLabel.getText()) + circleRadiusSpinner.getValue()) > BUS_RIGHT_BOUND || (Double.parseDouble(objectPositionYLabel.getText()) - circleRadiusSpinner.getValue()) < BUS_TOP_BOUND || (Double.parseDouble(objectPositionYLabel.getText()) + circleRadiusSpinner.getValue()) > BUS_BOTTOM_BOUND) {
                    throw new Exception();
                }
                // creates the CircleEntity
                CircleEntity c = new CircleEntity(Double.parseDouble(objectPositionXLabel.getText()), Double.parseDouble(objectPositionYLabel.getText()), circleRadiusSpinner.getValue(), rectangleMassSpinner.getValue());
                simList.add(c);
                cCount.add(c);

                Force gravity = new Force(c.position, x -> new Vector2D(0, c.mass * 9.81));
                c.extForces.add(gravity);

                MenuItem m = new MenuItem("Circle " + (cCount.indexOf(c) + 1));

                // handles events when a CircleEntity is removed
                m.setOnAction(event -> {
                    try {
                        simList.removeAll(springEntityLinkedToSimEnt.remove(c)); // removes all springs associated with the circle
                        removeObjectMenuButton.getItems().removeAll(menuItemsLinkedToSimEnt.remove(c)); // removes all menuItems associated to those springs
                    } catch (NullPointerException n) {
                    }
                    simList.remove(c); // removes circle from list of SimulationEntities
                    removeObjectMenuButton.getItems().remove(m); // removes the "remove" menuItem of the circle
                    springSelectObjectComboBox.getItems().remove(c); // removes the menuItem of the circle from add SpringEntity display
                    menuItemOfSimEnt.remove(c);
                    if (pauseButton.getText().equals("Resume")) {   // draws the new CircleEntity even if game is paused
                        for (SimulationEntity se : simList) {
                            canvas.getGraphicsContext2D().clearRect(0, 0, pane.getWidth(), pane.getHeight());
                            se.draw(canvas.getGraphicsContext2D());
                        }
                    }
                });
                // adds the new CircleEntity to all lists that track it and any other objects associated to it
                removeObjectMenuButton.getItems().add(m);
                springSelectObjectComboBox.getItems().add(c);
                menuItemOfSimEnt.put(c, m);

                if (pauseButton.getText().equals("Resume")) {
                    c.draw(canvas.getGraphicsContext2D());
                }
            } catch (NumberFormatException ex) { // handles creation error
                Alert alert = new Alert(Alert.AlertType.ERROR, "Select an initial spawn position for the circle!");
                alert.setTitle("ERROR!");
                alert.showAndWait();
            } catch (Exception ex) { // handles creation error
                Alert alert = new Alert(Alert.AlertType.ERROR, "Select an initial spawn position for the circle within the school bus!");
                alert.setTitle("ERROR!");
                alert.showAndWait();
            }
        });

        // handles events when a SpringEntity is created
        springAddButton.setOnAction(e -> {
            try { // ensures that circle is in bounds of bus
                if (Double.parseDouble(objectPositionXLabel.getText()) < BUS_LEFT_BOUND || Double.parseDouble(objectPositionXLabel.getText()) > BUS_RIGHT_BOUND || Double.parseDouble(objectPositionYLabel.getText()) < BUS_TOP_BOUND || Double.parseDouble(objectPositionYLabel.getText()) > BUS_BOTTOM_BOUND) {
                    throw new IllegalStateException();
                }
                // creates the SpringEntity
                SpringEntity s;
                if (springSelectObjectComboBox.getValue() instanceof RectangleEntity) { // in the case that spring is being added to a rectangle
                    s = new SpringEntity(Double.parseDouble(objectPositionXLabel.getText()), Double.parseDouble(objectPositionYLabel.getText()), springRectangleCornerComboBox.getValue());
                    Force springForce = new Force(springRectangleCornerComboBox.getValue(), x -> x.sub(s.origin).mult(STIFFNESS));
                    ((RectangleEntity) springSelectObjectComboBox.getValue()).extForces.add(springForce);
                    ((RectangleEntity) springSelectObjectComboBox.getValue()).move(s.origin.sub(springRectangleCornerComboBox.getValue()).mult(0.2));
                } else { // in the case that a spring is being added to a circle
                    s = new SpringEntity(Double.parseDouble(objectPositionXLabel.getText()), Double.parseDouble(objectPositionYLabel.getText()), ((CircleEntity) springSelectObjectComboBox.getValue()).position);
                    Force springForce = new Force(((CircleEntity) springSelectObjectComboBox.getValue()).position, x -> x.sub(s.origin).mult(STIFFNESS));
                    ((CircleEntity) springSelectObjectComboBox.getValue()).extForces.add(springForce);
                }
                simList.add(s);
                sCount.add(s);

                MenuItem m = new MenuItem("Line " + (sCount.indexOf(s) + 1));
                menuItemsLinkedToSimEnt.put(springSelectObjectComboBox.getValue(), m);
                springEntityLinkedToSimEnt.put(springSelectObjectComboBox.getValue(), s);

                // handles events when a CircleEntity is removed
                m.setOnAction(event -> {
                    simList.remove(s); // removes spring from list of SimulationEntitys
                    removeObjectMenuButton.getItems().remove(m);
                    if (pauseButton.getText().equals("Resume")) {
                        for (SimulationEntity se : simList) {
                            canvas.getGraphicsContext2D().clearRect(0, 0, pane.getWidth(), pane.getHeight());
                            se.draw(canvas.getGraphicsContext2D());
                        }
                    }
                });
                removeObjectMenuButton.getItems().add(m);

                if (pauseButton.getText().equals("Resume")) {
                    s.draw(canvas.getGraphicsContext2D());
                }
            } catch (IllegalStateException j) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Select an initial anchor point within the school bus!");
                alert.setTitle("ERROR!");
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Make sure to select an object to attach the spring to, a corner (only for rectangles), and an intial anchor point!");
                alert.setTitle("ERROR!");
                alert.showAndWait();
            }
        });

        springSelectObjectComboBox.setOnAction(e -> {
            if (springSelectObjectComboBox.getValue() instanceof RectangleEntity) {
                springRectangleCornerLabel.setVisible(true);
                springRectangleCornerComboBox.setVisible(true);
                RectangleEntity re = (RectangleEntity) springSelectObjectComboBox.getValue();
                springRectangleCornerComboBox.getItems().add(re.topLeft);
                springRectangleCornerComboBox.getItems().add(re.topRight);
                springRectangleCornerComboBox.getItems().add(re.bottomRight);
                springRectangleCornerComboBox.getItems().add(re.bottomLeft);
            } else {
                springRectangleCornerLabel.setVisible(false);
                springRectangleCornerComboBox.setVisible(false);
                springRectangleCornerComboBox.getItems().clear();
            }
        });

        // Declaration and initialization of the four wall that represent the bus boundaries
        WallEntity leftWall = new WallEntity(new Vector2D(BUS_LEFT_BOUND, BUS_TOP_BOUND), new Vector2D(BUS_LEFT_BOUND, BUS_BOTTOM_BOUND));
        WallEntity topWall = new WallEntity(new Vector2D(BUS_LEFT_BOUND, BUS_TOP_BOUND), new Vector2D(BUS_RIGHT_BOUND, BUS_TOP_BOUND));
        WallEntity rightWall = new WallEntity(new Vector2D(BUS_RIGHT_BOUND, BUS_TOP_BOUND), new Vector2D(BUS_RIGHT_BOUND, BUS_BOTTOM_BOUND));
        WallEntity bottomWall = new WallEntity(new Vector2D(BUS_LEFT_BOUND, BUS_BOTTOM_BOUND), new Vector2D(BUS_RIGHT_BOUND, BUS_BOTTOM_BOUND));
        simList.add(leftWall);
        simList.add(topWall);
        simList.add(rightWall);
        simList.add(bottomWall);

        pane.setBackground(new Background(new BackgroundImage(new Image("ragdoll/ragdollbackground.jpg"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        // AnimationTimer as main simulation loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (previousTime == 0) {
                    previousTime = now;
                    return;
                }

                float secondsElapsed = (now - previousTime) / 1e9f;
                float secondsElapsedCapped = Math.min(secondsElapsed, timeStep);
                accumulatedTime += secondsElapsedCapped;
                previousTime = now;

                // fixed timestep update implementation
                while (accumulatedTime >= timeStep) {

                    // nested for loops compare each SimulationEntity to all other entities and checks for collisions
                    for (int i = 0; i < simList.size(); i++) {
                        for (int j = i + 1; j < simList.size(); j++) {

                            try { // checks if two objects are colliding
                                if (simList.get(i).isColliding(simList.get(j))) {

                                    // in the case of a collision between two circles
                                    if (simList.get(i) instanceof CircleEntity && simList.get(j) instanceof CircleEntity) {
                                        CircleEntity c1 = (CircleEntity) simList.get(i);
                                        CircleEntity c2 = (CircleEntity) simList.get(j);


                                        double d = c2.position.sub(c1.position).magnitude();
                                        double normX = (c2.position.x - c1.position.x) / d;
                                        double normY = (c2.position.y - c1.position.y) / d;
                                        double p = 2 * (c1.velocity.x * normX + c1.velocity.y * normY - c2.velocity.x * normX - c2.velocity.y * normY) / (c1.mass + c2.mass);
                                        double c1NewVelocityX = c1.velocity.x - p * c1.mass * normX;
                                        double c1NewVelocityY = c1.velocity.y - p * c1.mass * normY;
                                        double c2NewVelocityX = c2.velocity.x + p * c2.mass * normX;
                                        double c2NewVelocityY = c2.velocity.y + p * c2.mass * normY;

                                        c1.velocity = new Vector2D(c1NewVelocityX, c1NewVelocityY);
                                        c2.velocity = new Vector2D(c2NewVelocityX, c2NewVelocityY);

                                        c1.position.x += c1NewVelocityX;
                                        c1.position.y += c1NewVelocityY;
                                        c2.position.x += c2NewVelocityX;
                                        c2.position.y += c2NewVelocityY;
                                    }

                                    // runs collision response if rectangle entity collision detection is toggled
                                    if (rectangleCollisionToggleButton.isSelected()) {

                                        // in the case of a collision between two rectangles
                                        if (simList.get(i) instanceof RectangleEntity && simList.get(j) instanceof RectangleEntity) {
                                                if (SAT.AABB) { // if the collision is AABB
                                                    RectangleEntity r1 = (RectangleEntity) simList.get(i);
                                                    Vector2D N = new Vector2D(0, -1);
                                                    N = N.norm();
                                                    r1.velocity = r1.velocity.sub(N.mult(2 * (r1.velocity.dot(N)))).mult(0.5);
                                                } else { // if the collision is SAT
                                                    RectangleEntity r1 = (RectangleEntity) simList.get(i);
                                                    Vector2D N = r1.center().sub(SAT.getCollisionPoint()); //.rotate(Math.PI , new Vector2D(0,0));
                                                    N = N.mult(1 / N.magnitude());
                                                    Vector2D Vr = r1.velocity;
                                                    Vector2D I = N.mult(-1 * (1 + 0.3) * Vr.dot(N));
                                                    r1.velocity = I;
                                                    r1.rotVelocity = -1 * 0.2 * (r1.rotVelocity / Math.abs(r1.rotVelocity)) * r1.center().sub(SAT.getCollisionPoint()).cross(Vr);
                                                }
                                        }
                                    }

                                    // runs collision response if environment collision detection is toggled (between WallEntity and another SimulationEntity)
                                    if (environmentCollisionToggleButton.isSelected()) {

                                        // in the case of a collision between a wall and rectangle
                                        if (simList.get(i) instanceof WallEntity && simList.get(j) instanceof RectangleEntity) {
                                            RectangleEntity r;
                                            SimulationEntity w;
                                            w = simList.get(i);
                                            r = ((RectangleEntity) simList.get(j));

                                            if (SAT.AABB) { // if the collision is AABB
                                                Vector2D N;
                                                if (w == leftWall) {
                                                    if (r.topLeft.x < BUS_LEFT_BOUND) {
                                                        r.move(new Vector2D((BUS_LEFT_BOUND - r.topLeft.x), 0));
                                                    }
                                                    N = new Vector2D(1, 0);
                                                    N = N.norm();
                                                    r.velocity = r.velocity.sub(N.mult((2 * r.velocity.dot(N))));
                                                } else if (w == topWall) {
                                                    if (r.topLeft.y < BUS_TOP_BOUND) {
                                                        r.move(new Vector2D(0, (BUS_TOP_BOUND - r.topLeft.y)));
                                                    }
                                                    N = new Vector2D(0, 1);
                                                    N = N.norm();
                                                    r.velocity = r.velocity.sub(N.mult((2 * r.velocity.dot(N))));
                                                } else if (w == rightWall) {
                                                    if (r.topRight.x > BUS_RIGHT_BOUND) {
                                                        r.move(new Vector2D((r.topRight.x - BUS_RIGHT_BOUND), 0));
                                                    }
                                                    N = new Vector2D(-1, 0);
                                                    N = N.norm();
                                                    r.velocity = r.velocity.sub(N.mult((2 * r.velocity.dot(N))));
                                                } else {
                                                    if (r.bottomLeft.y > BUS_BOTTOM_BOUND) {
                                                        r.move(new Vector2D(0, -(r.bottomLeft.y - BUS_BOTTOM_BOUND) * 0.9));
                                                    }
                                                    N = new Vector2D(0, -1);
                                                    N = N.norm();
                                                    r.velocity = r.velocity.sub(N.mult((2 * r.velocity.dot(N)))).mult(0.5);
                                                } if (r.velocity.magnitude() < 0.3) {
                                                    r.velocity = new Vector2D(0,0);
                                                }
                                            } else {
                                                Vector2D N = r.center().sub(SAT.getCollisionPoint()); //.rotate(Math.PI , new Vector2D(0,0));
                                                N = N.mult(1 / N.magnitude());
                                                Vector2D Vr = r.velocity;
                                                Vector2D I = N.mult(-1 * (1 + 0.3) * Vr.dot(N));
                                                r.rotVelocity = -1 * 0.2 * (r.rotVelocity / Math.abs(r.rotVelocity)) * r.center().sub(SAT.getCollisionPoint()).cross(Vr);
                                            }
                                        }

                                        // in the case of a collision between a wall and circle
                                        if (simList.get(i) instanceof WallEntity && simList.get(j) instanceof CircleEntity) {
                                            WallEntity w = (WallEntity) simList.get(i);
                                            CircleEntity c = (CircleEntity) simList.get(j);

                                            Vector2D N;
                                            if (w == leftWall) {
                                                if ((c.position.x - c.radius) < BUS_LEFT_BOUND) {
                                                    c.move(new Vector2D((BUS_LEFT_BOUND - (c.position.x - c.radius)), 0));
                                                }
                                                N = new Vector2D(1, 0);
                                                N = N.norm();
                                                c.velocity = c.velocity.sub(N.mult((2 * c.velocity.dot(N))));
                                            } else if (w == topWall) {
                                                if ((c.position.y - c.radius) < BUS_TOP_BOUND) {
                                                    c.move(new Vector2D(0, (BUS_TOP_BOUND - (c.position.y - c.radius))));
                                                }
                                                N = new Vector2D(0, 1);
                                                N = N.norm();
                                                c.velocity = c.velocity.sub(N.mult((2 * c.velocity.dot(N))));
                                            } else if (w == rightWall) {
                                                if ((c.position.x + c.radius) > BUS_RIGHT_BOUND) {
                                                    c.move(new Vector2D(((c.position.x + c.radius) - BUS_RIGHT_BOUND), 0));
                                                }
                                                N = new Vector2D(-1, 0);
                                                N = N.norm();
                                                c.velocity = c.velocity.sub(N.mult((2 * c.velocity.dot(N))));
                                            } else {
                                                if ((c.position.y + c.radius) > BUS_BOTTOM_BOUND) {
                                                    c.move(new Vector2D(0, -((c.position.y + c.radius) - BUS_BOTTOM_BOUND) * 0.9));
                                                }
                                                N = new Vector2D(0, -1);
                                                N = N.norm();
                                                c.velocity = c.velocity.sub(N.mult((2 * c.velocity.dot(N)))).mult(0.5);
                                            } if (c.velocity.magnitude() < 0.3) {
                                                c.velocity = new Vector2D(0,0);
                                            }

                                        }
                                    }
                                }
                            } catch (Exception ex) { // catch any exception in the collision detection/response
                                System.out.println("A collision detection or collision response error has occurred.");
                            }
                        }
                        if (simList.get(i) instanceof RectangleEntity) { // removes RectangleEntity if it has travelled significantly out of bounds
                            RectangleEntity r = (RectangleEntity) simList.get(i);
                            if (r.topLeft.x < -3000 || r.topLeft.x > 3720 || r.topLeft.y < -3000 || r.topLeft.y > 3571) {
                                menuItemOfSimEnt.get(r).fire();
                            }
                        } else if (simList.get(i) instanceof CircleEntity) {  // removes CircleEntity if it has travelled significantly out of bounds
                            CircleEntity c = (CircleEntity) simList.get(i);
                            if (c.position.x < -3000 || c.position.x > 3720 || c.position.y < -3000 || c.position.y > 3571) {
                                menuItemOfSimEnt.get(c).fire();
                            }
                        }
                    }

                    // loops through all SimulationEntity objects again and updates them all
                    for (SimulationEntity e : simList) {
                        e.update(timeStep);
                    }
                    accumulatedTime -= timeStep;
                }
                canvas.getGraphicsContext2D().clearRect(0, 0, pane.getWidth(), pane.getHeight()); // clears canvas

                // loops through all SimulationEntity objects and draws (renders) them all
                for (SimulationEntity e : simList) {
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    e.draw(gc);
                }

                // event handler for pause button
                pauseButton.setOnAction(e -> {
                    this.stop(); // pauses AnimationTimer
                    pauseButton.setText("Resume");
                    pauseButton.setOnAction(d -> {
                        this.start(); // starts AnimationTimer
                        pauseButton.setText("Pause");
                    });
                });
            }
        }.start();
    }
}