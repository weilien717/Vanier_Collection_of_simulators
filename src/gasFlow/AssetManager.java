package gasflow;

import java.io.File;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class AssetManager {

    static private Background backgroundImage = null;

    static private String fileURL(String relativePath) {
        return new File(relativePath).toURI().toString();
    }

    static public void preloadAllAssets() {
        // Set BackGround Image to be white rectangle
        backgroundImage = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
    }

    static public Background getBackgroundImage() {
        return backgroundImage;
    }
}
