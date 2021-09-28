package buoyancy;

import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.ImagePattern;

public class AssetManager {

    static private Background backgroundImage = null;
    static private Background backgroundImage_WaterBuoyancySim = null;
    static private ImagePattern glassImage = null;
    static private ImagePattern cottonWoodBox = null;
    static private ImagePattern iceCubeBoxImage = null;
    static private ImagePattern BrickImage = null;
    static private ImagePattern ebonyWoodBoxImage = null;

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
        
        
        Image background_WaterBuoyancySim = new Image(fileURL("./assets/images/bubble.png"));
        backgroundImage_WaterBuoyancySim = new Background(
                new BackgroundImage(background_WaterBuoyancySim,
                        BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT));
        
        
        glassImage = new ImagePattern(new Image(fileURL("./assets/images/Glass.png")));
        cottonWoodBox = new ImagePattern(new Image(fileURL("./assets/images/cottonWoodBox.jpg")));
        iceCubeBoxImage = new ImagePattern(new Image(fileURL("./assets/images/iceCube.png")));
        BrickImage = new ImagePattern(new Image(fileURL("./assets/images/brick.png")));
        ebonyWoodBoxImage = new ImagePattern(new Image(fileURL("./assets/images/ebonyWoodBox.png")));
       
       
    }
    
    
   static public Background getBackgroundImage() {
       return backgroundImage;
   }
   
   static public Background getWaterBuoyancySim_BackgroundImage() {
       return backgroundImage_WaterBuoyancySim;
   }
   
   static public ImagePattern getGlassImage() {
        return glassImage;
    }
   
   static public ImagePattern getCottonWoodBoxImage() {
        return cottonWoodBox;
    }
   static public ImagePattern getIceCubeImage() {
        return iceCubeBoxImage;
    }
   static public ImagePattern getBrickImage() {
        return BrickImage;
    }  
   
   static public ImagePattern getEbonyWoodBoxImage() {
        return ebonyWoodBoxImage;
    }


   
}
