package buoyancy;

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class BuoyancySimPane extends AnchorPane implements EventHandler<ActionEvent> {

    private ArrayList<GameObject> objectList = new ArrayList<>();
    private AnimationTimer animationTimer = null;
    private double lastFrameTime = 0.0;
    private Button GoButton;
    private Label resultLabel;
    private TextField saltTextField;
    ;
    private TextField volumeTextField;
    private Rectangle water;
    private ComboBox materialComboBoxlBox;
    private Mass objectCube;
    private String currentMaterial = null;
    private final double Fg = 9.8;
    private double accelerationOfObject = 0;
    double volumeOfObjectSubmerged_m3 = 0.001;
    private boolean givenAcceleration;
    private double liquidLevel = 310;
    private double volumeAboveLiquidLevel = 0;
    private double heightOfObjectSubmerged = 70;
    private FBD_Value buoyantForceRectangle;
    private FBD_Value normalForceRectangle;
    private FBD_Value FgRectangle;
    private Label feedbackLabel;

    public BuoyancySimPane() {
        initialize();

        this.setOnKeyPressed(this::onKeyPressed);
        this.setOnKeyReleased(this::onKeyReleased);
        this.setOnMouseMoved(this::onMouseMoved);
        this.setOnMouseClicked(this::onMouseClicked);
    }

    public void increaseSolution() {

    }

    public void addToPane(Node node) {
        this.getChildren().add(node);
    }

    public void removeFromPane(Node node) {
        this.getChildren().remove(node);
    }

    private void onKeyPressed(KeyEvent e) {
        //  System.out.println("Key Pressed");
    }

    private void onKeyReleased(KeyEvent e) {
        //  System.out.println("Key Released");
    }

    private void onMouseClicked(MouseEvent e) {
        //  System.out.println("Mouse Clicked");
    }

    private void onMouseMoved(MouseEvent e) {
        //  System.out.println(e.getX() + " ; " + e.getY());
    }

    public void initialize() {

        AssetManager.preloadAllAssets();
        this.setBackground(AssetManager.getWaterBuoyancySim_BackgroundImage());
        lastFrameTime = 0.0f;
        long initialTime = System.nanoTime();

        // Title label
        Label title = new Label();
        title.setPrefSize(300, 50);
        title.setLayoutX(80);
        title.setLayoutY(20);
        title.setText("Buoyancy Simulator! ");
        title.setFont(new Font("Arial", 30));
        title.setTextFill(Color.WHITE);
        addToPane(title);
        
        // Assumption: each side of  cube is 10cm
        Label assumptionLabel = new Label();
        assumptionLabel.setPrefSize(200, 50);
        assumptionLabel.setLayoutX(100);
        assumptionLabel.setLayoutY(160);
        assumptionLabel.setFont(new Font("Arial", 14));
        assumptionLabel.setText("Each side of cube is 10 cm");
        assumptionLabel.setTextFill(Color.BISQUE);
        addToPane(assumptionLabel);

        // Instruction Labels
        Label yellowInstructionLabel = new Label();
        yellowInstructionLabel.setPrefSize(180, 20);
        yellowInstructionLabel.setLayoutX(200);
        yellowInstructionLabel.setLayoutY(535);
        yellowInstructionLabel.setText("Buoyant Force : Yellow");
        yellowInstructionLabel.setFont(new Font("Arial", 14));
        yellowInstructionLabel.setTextFill(Color.YELLOW);
        addToPane(yellowInstructionLabel);

        Label brownInstructionLabel = new Label();
        brownInstructionLabel.setPrefSize(150, 20);
        brownInstructionLabel.setLayoutX(200);
        brownInstructionLabel.setLayoutY(555);
        brownInstructionLabel.setText("Normal Force : Brown");
        brownInstructionLabel.setFont(new Font("Arial", 14));
        brownInstructionLabel.setTextFill(Color.BROWN);
        addToPane(brownInstructionLabel);

        Label WhiteInstructionLabel = new Label();
        WhiteInstructionLabel.setPrefSize(150, 20);
        WhiteInstructionLabel.setLayoutX(200);
        WhiteInstructionLabel.setLayoutY(575);
        WhiteInstructionLabel.setText("Fg : White");
        WhiteInstructionLabel.setFont(new Font("Arial", 14));
        WhiteInstructionLabel.setTextFill(Color.WHITE);
        addToPane(WhiteInstructionLabel);

        // Initialize the glass container
        Rectangle glass = new Rectangle(250, 285);
        glass.setX(100);
        glass.setY(235);
        glass.setFill(Color.TRANSPARENT);
        glass.setStroke(Color.BLACK);
        addToPane(glass);

        // Salt Label
        Label saltLabel = new Label();
        saltLabel.setText("Quantity of salt to add (g)");
        saltLabel.setTextFill(Color.WHITE);
        saltLabel.setPrefSize(150, 60);
        saltLabel.setLayoutX(420);
        saltLabel.setLayoutY(90);
        addToPane(saltLabel);

        // Salt TextField
        saltTextField = new TextField("0");
        saltTextField.setPrefSize(50, 30);
        saltTextField.setLayoutX(580);
        saltTextField.setLayoutY(105);
        addToPane(saltTextField);

        // Volume label
        Label volumeLabel = new Label();
        volumeLabel.setText("Volume of liquid :                    1000 cm^3 ");
        volumeLabel.setTextFill(Color.WHITE);
        volumeLabel.setPrefSize(250, 60);
        volumeLabel.setLayoutX(420);
        volumeLabel.setLayoutY(150);
        addToPane(volumeLabel);

        // Initialize the liquid solution
        water = new Rectangle(glass.getWidth(), glass.getHeight() - 80);
        water.setX(glass.getX());
        water.setY(315);
        water.setFill(Color.BLUE);
        addToPane(water);

        // GO button
        GoButton = new Button();
        GoButton.setText("Go!");
        GoButton.setPrefSize(100, 50);
        GoButton.setLayoutX(500);
        GoButton.setLayoutY(500);
        GoButton.setOnAction(this);
        addToPane(GoButton);

        // Result label to display data
        resultLabel = new Label("");
        resultLabel.setPrefSize(300, 100);
        resultLabel.setLayoutX(420);
        resultLabel.setLayoutY(350);
        resultLabel.setFont(new Font("Arial", 13));
        resultLabel.setTextFill(Color.WHITE);
        addToPane(resultLabel);
        
        // Feedback label
        feedbackLabel = new Label("");
        feedbackLabel.setPrefSize(300, 150);
        feedbackLabel.setLayoutX(420);
        feedbackLabel.setLayoutY(250);
        feedbackLabel.setFont(new Font("Arial", 20));
        feedbackLabel.setTextFill(Color.LIGHTGOLDENRODYELLOW);
        addToPane(feedbackLabel);
        
        // CheckBox to replace Water to Oil 
        CheckBox oilModeCheckBox = new CheckBox();
        oilModeCheckBox.setText("Oil");
        oilModeCheckBox.setLayoutX(100);
        oilModeCheckBox.setLayoutY(520);
        oilModeCheckBox.setPrefSize(100, 100);
        oilModeCheckBox.setFont(new Font("Arial", 15));
        addToPane(oilModeCheckBox);

        // Initialize material list
        ObservableList<String> materialList
                = FXCollections.observableArrayList(
                        "CottonWood (400kg/m^3)",
                        "Ice Cube (920kg/m^3)",
                        "EbonyWood (1200kg/m^3)",
                        "Brick (2000kg/m^3)");

        materialComboBoxlBox = new ComboBox();
        materialComboBoxlBox.setPrefSize(220, 60);
        materialComboBoxlBox.setLayoutX(420);
        materialComboBoxlBox.setLayoutY(220);
        materialComboBoxlBox.setItems(materialList);
        materialComboBoxlBox.getSelectionModel().selectFirst();
        addToPane(materialComboBoxlBox);

        // Initialize the cube in the middle of the container
        objectCube = new Mass(new Vector2D(195, 380), materialComboBoxlBox.getValue().toString());
        objectList.add(objectCube);
        addToPane(objectCube.getRectangle());
        currentMaterial = materialComboBoxlBox.getValue().toString();

        // Initialize the Free Body Diagram 
        Circle FBD_Point = new Circle(5);
        FBD_Point.setLayoutX(40);
        FBD_Point.setLayoutY(350);
        addToPane(FBD_Point);

        buoyantForceRectangle = new FBD_Value(new Vector2D(0, 0));
        buoyantForceRectangle.getRectangle().setWidth(10);
        addToPane(buoyantForceRectangle.getRectangle());
        objectList.add(buoyantForceRectangle);

        normalForceRectangle = new FBD_Value(new Vector2D(0, 0));
        normalForceRectangle.getRectangle().setWidth(10);
        addToPane(normalForceRectangle.getRectangle());
        objectList.add(normalForceRectangle);

        FgRectangle = new FBD_Value(new Vector2D(0, 0));
        FgRectangle.getRectangle().setWidth(10);
        addToPane(FgRectangle.getRectangle());
        objectList.add(FgRectangle);

        // Start the animation
        if (animationTimer != null) {
            animationTimer.stop();
        }
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double massOfObject_kg = 0;
                double massSalt = 0;
                double weightOfObject = 0;
                double buoyantForce = 0;
                double densityOfSolution_kg_m3 = 0;
                double densityOfObject_kg_m3 = 0;
                double volumeSolution_m3 = 0.001;
                double normalForce = 0;
                boolean objectAtTheBottom = false;

                // Time calculation                
                double currentTime = (now - initialTime) / 1000000000.0;
                double frameDeltaTime = currentTime - lastFrameTime;
                lastFrameTime = currentTime;

                // Game logic
                for (GameObject obj : objectList) {
                    obj.update(frameDeltaTime);
                }

                // If the material choice is changed, then reset the values and the position of the cube
                if (materialComboBoxlBox.getValue().toString() != currentMaterial) {
                    removeFromPane(objectCube.getRectangle());
                    objectList.remove(objectCube);
                    objectCube = new Mass(new Vector2D(195, 380), materialComboBoxlBox.getValue().toString());
                    objectList.add(objectCube);
                    addToPane(objectCube.getRectangle());
                    currentMaterial = materialComboBoxlBox.getValue().toString();

                    //reset the acceleration and the volume submerged
                    givenAcceleration = false;
                    volumeOfObjectSubmerged_m3 = 0.001;
                    accelerationOfObject = 0;
                    heightOfObjectSubmerged = 70;
                }

                // Set the density of the object based on the material chosen
                if (materialComboBoxlBox.getValue().equals("CottonWood (400kg/m^3)")) {
                    densityOfObject_kg_m3 = 400;
                } else if (materialComboBoxlBox.getValue().equals("Ice Cube (920kg/m^3)")) {
                    densityOfObject_kg_m3 = 920;
                } else if (materialComboBoxlBox.getValue().equals("EbonyWood (1200kg/m^3)")) {
                    densityOfObject_kg_m3 = 1200;
                } else if (materialComboBoxlBox.getValue().equals("Brick (2000kg/m^3)")) {
                    densityOfObject_kg_m3 = 2000;
                }

                // Calculation:
                // Volume of the object submerge in the water, We assume that the cube has 10 cm in each side of cube to have a volume = 0.001 m^3
                volumeOfObjectSubmerged_m3 = (objectCube.getRectangle().getWidth() * objectCube.getRectangle().getHeight() * heightOfObjectSubmerged) * (0.001 / 343000);

                // Mass of the object 
                massOfObject_kg = densityOfObject_kg_m3 * volumeSolution_m3;

                // Weight of the object
                double pre_weightOfObject = massOfObject_kg * 9.8;
                weightOfObject = Math.round(pre_weightOfObject * 100) / 100.0;

                // Density of the solution
                try {
                    massSalt = Double.parseDouble(saltTextField.getText());
                    
                    // If the mass entered is negative, then set it to 0
                    if (massSalt < 0) {
                        massSalt = 0;
                        saltTextField.setText("0");
                    }
                    
                    // If the mass entered surpassses 5000, then set it to 5000
                    if (massSalt >= 5000) {
                        massSalt = 5000;
                        saltTextField.setText("5000");
                    }
                    feedbackLabel.setText("");
                } catch (Exception e) {
                    if (!saltTextField.getText().equals("")) {
                        feedbackLabel.setText("Please enter a valid quantity!");
                    }
                    
                        feedbackLabel.setText("Please enter a valid quantity!");;
                    
                }
                
                // Calculation for the density of the solution
                double massOfSolution = 0;
                
                // If the oil mode is selected, then the density of the solution is 0.93g/cm3
                if (oilModeCheckBox.isSelected()) {
                    water.setFill(Color.GOLD);
                    massOfSolution = volumeSolution_m3 * 0.93;
                } else {
                    massOfSolution = volumeSolution_m3;
                    water.setFill(Color.BLUE);
                }
                
                // Calculate the density of the solution
                // round the value to 2 digits after the decimal
                double pre_densityOfSolution_kg_m3 = (massSalt / 1000 + massOfSolution * 1000) / volumeSolution_m3;
                densityOfSolution_kg_m3 = Math.round(pre_densityOfSolution_kg_m3 * 100) / 100.0;

                // Buoyant Force
                double pre_buoyantForce = densityOfSolution_kg_m3 * Fg * volumeOfObjectSubmerged_m3;
                buoyantForce = Math.round(pre_buoyantForce * 100) / 100.0;

                // Acceleration of Block
                accelerationOfObject = (buoyantForce - weightOfObject) / massOfObject_kg;

                // Update the volume of object that is submerged in the solution
                for (int i = 0; i < objectList.size(); i++) {
                    if (objectList.get(i) instanceof Mass) {

                        // Update the submerged volume of the object when it floats on the surface
                        if (objectList.get(i).getPosition().getY() <= liquidLevel) {
                            volumeAboveLiquidLevel = liquidLevel - objectList.get(i).getPosition().getY();
                            double newObjectHeight = objectCube.getRectangle().getHeight() - volumeAboveLiquidLevel;
                            heightOfObjectSubmerged = newObjectHeight;
                        }

                        // Initialize the normal force when reaching the bottom of the glass
                        if ((objectList.get(i).getPosition().getY() + objectList.get(i).getRectangle().getHeight()) >= (glass.getY() + glass.getHeight())) {
                            objectAtTheBottom = true;
                            if (objectAtTheBottom && buoyantForce < weightOfObject) {
                                double pre_NormalForce = weightOfObject - buoyantForce;
                                normalForce = Math.round(pre_NormalForce * 100) / 100.0;
                                accelerationOfObject = (buoyantForce - weightOfObject + normalForce) / massOfObject_kg;

                            }
                        } else {
                            objectAtTheBottom = false;
                        }
                        
                        
                        if ((buoyantForce - weightOfObject) == 0) {
                            objectList.get(i).setAcceleration(new Vector2D(0, 0));
                            objectList.get(i).setVelocity(new Vector2D(0, 0));
                        }
                        
                        // Set the acceleration
                        if (givenAcceleration) {
                            objectList.get(i).setAcceleration(new Vector2D(0, -accelerationOfObject));
                            objectList.get(i).setVelocity(new Vector2D(0, -accelerationOfObject * 20));
                        }
                    }

                }
                
                // Draw the free body diagram
                
                buoyantForceRectangle.getRectangle().setHeight(buoyantForce * 5);
                buoyantForceRectangle.setPosition(new Vector2D(FBD_Point.getLayoutX() - buoyantForceRectangle.getRectangle().getWidth() / 2, FBD_Point.getLayoutY() - buoyantForceRectangle.getRectangle().getHeight() - 5));
                buoyantForceRectangle.getRectangle().setFill(Color.YELLOW);

                FgRectangle.getRectangle().setHeight(weightOfObject * 5);
                FgRectangle.getRectangle().setFill(Color.WHITE);
                FgRectangle.setPosition(new Vector2D(FBD_Point.getLayoutX() - FgRectangle.getRectangle().getWidth() / 2, FBD_Point.getLayoutY() + 5));

                if (objectAtTheBottom) {
                    normalForceRectangle.getRectangle().setFill(Color.BROWN);
                    normalForceRectangle.getRectangle().setHeight(normalForce * 5);
                    normalForceRectangle.setPosition(new Vector2D(FBD_Point.getLayoutX() - normalForceRectangle.getRectangle().getWidth() / 2, buoyantForceRectangle.getPosition().getY() - normalForceRectangle.getRectangle().getHeight()));
                    normalForceRectangle.getRectangle().setVisible(true);
                } else {
                    normalForceRectangle.getRectangle().setVisible(false);
                }

                // Display all data
                resultLabel.setText("Mass of the object = " + massOfObject_kg + " kg"
                        + "\nDensity of Solution " + densityOfSolution_kg_m3 + "kg / m^3"
                        + "\nObject Weight = " + weightOfObject + "N"
                        + "\nBuoyant Force = " + buoyantForce + " N"
                        + "\nNormal Force = " + normalForce + "N"
                );
            }
        };
        animationTimer.start();

    }

    // GObuttonAction
    public void handle(ActionEvent event) {
        for (int i = 0; i < objectList.size(); i++) {
            givenAcceleration = true;

        }
    }

}
