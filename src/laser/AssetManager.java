package laser;

import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

public class AssetManager {

    static private Background backgroundImage = null;

    static private String fileURL(String relativePath) {
        return new File(relativePath).toURI().toString();
    }

    static public void preloadAllAssets() {
        // Preload all images
        Image background = new Image(fileURL("./assets/gasSim/images/background.png"));
        backgroundImage = new Background(
                new BackgroundImage(background,
                        BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT));
    }

    static public Background getBackgroundImage() {
        return backgroundImage;
    }
}
