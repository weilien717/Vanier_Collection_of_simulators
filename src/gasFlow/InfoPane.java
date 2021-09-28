package gasflow;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class InfoPane extends GameObject {

    private GasSimPane gsp;

    private Volume volume = null;

    private Label titleLabel;
    private Label formulaLabel;
    private Label pressureLabel;
    private TextField pressureTextField;
    private Label volumeLabel;
    private TextField volumeTextField;
    private Label molesLabel;
    private TextField molesTextField;
    private Label temperatureLabel;
    private TextField temperatureTextField;
    private Label h2PercentageLabel;
    private TextField h2PercentageTextField;
    private Label o2PercentageLabel;
    private TextField o2PercentageTextField;
    private Label n2PercentageLabel;
    private TextField n2PercentageTextField;
    private Button deleteButton;

    ArrayList<Node> items;

    public InfoPane(Vector2D size, GasSimPane gsp, Volume volume) {
        //Constructor, initializes infopane
        super(new Vector2D(0, 0), new Vector2D(0, 0), new Vector2D(0, 0), size, null);
        this.volume = volume;

        this.getPosition().setX((gsp.widthProperty().subtract(this.getSize().getX() + 10)).doubleValue());
        this.getPosition().setY(10);

        getRect().setFill(Color.ORANGE);
        getRect().setStroke(Color.ORANGERED);
        getRect().setStrokeWidth(2);

        this.gsp = gsp;

        titleLabel = new Label("Volume Info\n");
        formulaLabel = new Label("PV = nRT\n\n");
        pressureLabel = new Label("Pressure (atm) : ");
        pressureTextField = new TextField("0");
        volumeLabel = new Label("Volume (m^3) : ");
        volumeTextField = new TextField("0");
        molesLabel = new Label("Moles (moles) : ");
        molesTextField = new TextField("0");
        temperatureLabel = new Label("Temperature (K) : ");
        temperatureTextField = new TextField("0");
        h2PercentageLabel = new Label("\nH2 amount (%) : ");
        h2PercentageTextField = new TextField("100");
        o2PercentageLabel = new Label("O2 amount (%) : ");
        o2PercentageTextField = new TextField("0");
        n2PercentageLabel = new Label("N2 amount (%) : ");
        n2PercentageTextField = new TextField("0");
        deleteButton = new Button("Delete Volume");

        items = new ArrayList<>();

        updateItems();
    }

    public Volume getVolume() {
        return volume;
    }

    private void updateItems() {
        //Updates the values of items in infopane
        int tempSize = items.size();
        for (int i = 0; i < tempSize; i++) {
            items.remove(0);
        }

        items.add(titleLabel);
        items.add(formulaLabel);
        items.add(pressureLabel);
        items.add(pressureTextField);
        items.add(volumeLabel);
        items.add(volumeTextField);
        items.add(molesLabel);
        items.add(molesTextField);
        items.add(temperatureLabel);
        items.add(temperatureTextField);
        items.add(h2PercentageLabel);
        items.add(h2PercentageTextField);
        items.add(o2PercentageLabel);
        items.add(o2PercentageTextField);
        items.add(n2PercentageLabel);
        items.add(n2PercentageTextField);
        items.add(deleteButton);

        for (int i = 0; i < items.size(); i++) {
            items.get(i).setLayoutX(this.getPosition().getX() + 10);
            if (i > 0) {
                if (i == 1) {
                    items.get(i).setLayoutY(items.get(i - 1).getLayoutY() + 1.5 * items.get(i - 1).maxHeight(0) + 3);
                } else {
                    items.get(i).setLayoutY(items.get(i - 1).getLayoutY() + items.get(i - 1).maxHeight(0) + 3);
                }
            } else {
                items.get(i).setLayoutY(this.getPosition().getY() + 10);
            }
            items.get(i).toFront();
        }
    }

    public void setVolume(Volume volume) {
        //attributes a volume whos info will be displayed in infopane
        this.volume = volume;

        titleLabel = new Label("Volume Info\n");                                                    //titleLabel
        formulaLabel = new Label("PV = nRT\n\n");
        pressureLabel = new Label("Pressure (atm) : ");                                             //pressureLabel
        pressureTextField = new TextField(String.format("%.2f", this.volume.getPressure()));        //pressureTextField
        pressureTextField.setPrefWidth(100);
        pressureTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    double temp = Double.parseDouble(pressureTextField.getText());
                    System.out.println("Pressure Being Modified");
                    volume.setPressure(temp);
                    updateValues();
                } catch (Exception e) {
                    System.out.println("Improper input: Input must be a number");
                    gsp.instructionLabel.setText("Improper input: Input must be a number");
                }
            }
        });
        volumeLabel = new Label("Volume (m^3) : ");                                                 //volumeLabel
        volumeTextField = new TextField(String.format("%.2f", this.volume.getVolume()));            //volumeTextField
        volumeTextField.setDisable(true);
        volumeTextField.setPrefWidth(100);
        molesLabel = new Label("Moles (moles) : ");                                                 //molesLabel
        molesTextField = new TextField(String.format("%.2f", this.volume.getMoles()));              //molesTextField
        molesTextField.setPrefWidth(100);
        molesTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    double temp = Double.parseDouble(molesTextField.getText());
                    System.out.println("Moles Being Modified");
                    volume.setMoles(temp);
                    updateValues();
                } catch (Exception e) {
                    System.out.println("Improper input: Input must be a number");
                    gsp.instructionLabel.setText("Improper input: Input must be a number");
                }
            }
        });
        temperatureLabel = new Label("Temperature (K) : ");                                         //temperatureLabel
        temperatureTextField = new TextField(String.format("%.2f", this.volume.getTemperature()));  //temperatureTextField
        temperatureTextField.setPrefWidth(100);
        temperatureTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    double temp = Double.parseDouble(temperatureTextField.getText());
                    System.out.println("Temperature Being Modified");
                    volume.setTemperature(temp);
                    updateValues();
                } catch (Exception e) {
                    System.out.println("Improper input: Input must be a number");
                    gsp.instructionLabel.setText("Improper input: Input must be a number");
                }
            }
        });
        h2PercentageLabel = new Label("\nH2 amount (%) : ");
        h2PercentageTextField = new TextField(String.format("%.2f", this.volume.getH2Percentage()));
        h2PercentageTextField.setPrefWidth(100);
        h2PercentageTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    double temp = Double.parseDouble(h2PercentageTextField.getText());
                    System.out.println("H2 Percentage Being Modified");
                    volume.setH2Percentage(temp);
                    updateValues();
                } catch (Exception e) {
                    System.out.println("Improper input: Input must be a number");
                    gsp.instructionLabel.setText("Improper input: Input must be a number");
                }
            }
        });
        o2PercentageLabel = new Label("O2 amount (%) : ");
        o2PercentageTextField = new TextField(String.format("%.2f", this.volume.getO2Percentage()));
        o2PercentageTextField.setPrefWidth(100);
        o2PercentageTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    double temp = Double.parseDouble(o2PercentageTextField.getText());
                    System.out.println("O2 Percentage Being Modified");
                    volume.setO2Percentage(temp);
                    updateValues();
                } catch (Exception e) {
                    System.out.println("Improper input: Input must be a number");
                    gsp.instructionLabel.setText("Improper input: Input must be a number");
                }
            }
        });
        n2PercentageLabel = new Label("N2 amount (%) : ");
        n2PercentageTextField = new TextField(String.format("%.2f", this.volume.getN2Percentage()));
        n2PercentageTextField.setPrefWidth(100);
        n2PercentageTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    double temp = Double.parseDouble(n2PercentageTextField.getText());
                    System.out.println("N2 Percentage Being Modified");
                    volume.setN2Percentage(temp);
                    updateValues();
                } catch (Exception e) {
                    System.out.println("Improper input: Input must be a number");
                    gsp.instructionLabel.setText("Improper input: Input must be a number");
                }
            }
        });
        deleteButton = new Button("Delete Volume");                                                 //deleteButton
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Deleting Volume");
                gsp.removeVolume(volume);
                gsp.hideInfoPane();
            }
        });

        updateItems();
    }

    public void updateValues() {
        //updates only the values
        pressureTextField.setText(String.format("%.2f", this.volume.getPressure()));
        volumeTextField.setText(String.format("%.2f", this.volume.getVolume()));
        molesTextField.setText(String.format("%.2f", this.volume.getMoles()));
        temperatureTextField.setText(String.format("%.2f", this.volume.getTemperature()));
        h2PercentageTextField.setText(String.format("%.2f", this.volume.getH2Percentage()));
        o2PercentageTextField.setText(String.format("%.2f", this.volume.getO2Percentage()));
        n2PercentageTextField.setText(String.format("%.2f", this.volume.getN2Percentage()));
    }

    public ArrayList<Node> getItems() {
        //returns a list of the Nodes in the infopane
        int tempSize = items.size();
        for (int i = 0; i < tempSize; i++) {
            items.remove(0);
        }
        items.add(titleLabel);
        items.add(formulaLabel);
        items.add(pressureLabel);
        items.add(pressureTextField);
        items.add(volumeLabel);
        items.add(volumeTextField);
        items.add(molesLabel);
        items.add(molesTextField);
        items.add(temperatureLabel);
        items.add(temperatureTextField);
        items.add(h2PercentageLabel);
        items.add(h2PercentageTextField);
        items.add(o2PercentageLabel);
        items.add(o2PercentageTextField);
        items.add(n2PercentageLabel);
        items.add(n2PercentageTextField);
        items.add(deleteButton);

        return items;
    }

    @Override
    public void update(double dt) {
        //is called at every frame
        super.update(dt);
        this.getPosition().setX((gsp.widthProperty().subtract(this.getSize().getX() + 10)).doubleValue());
        this.getPosition().setY(10);
        this.getRect().toFront();

        updateItems();
    }
}
