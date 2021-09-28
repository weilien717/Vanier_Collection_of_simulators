package ragdoll;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class RagdollSimPane extends AnchorPane {

    public RagdollSimPane() {
        super();
        this.loadFXML();
    }

    public Parent loadFXML() {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLDocumentRagdoll.fxml"));
            this.getChildren().add(pane);
            return pane;
        } catch (Exception e) {
            System.out.println("somtin went wron");
        }
        return null;
    }
}
