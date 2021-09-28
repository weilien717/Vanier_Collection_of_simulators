import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CollectionOfSimulations extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

        Scene scene = new Scene(root);

        scene.setOnKeyPressed(root.getOnKeyPressed());

        stage.setResizable(false);
        stage.setMaxWidth(726);
        stage.setMaxHeight(671);

        stage.setTitle("Collection of Simulations");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
