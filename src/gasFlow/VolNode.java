package gasflow;

import javafx.scene.paint.Color;

public class VolNode extends GameObject {

    private Boolean hasFocus;
    private static final double WIDTH = 10;
    private VolumeLink volumeLink = null;
    private Boolean isConnected = false;
    private final Volume volume;

    public VolNode(Vector2D position, Volume volume) {
        //creates a new volume node
        //volumeNodes are what connects volume to volume links and vice versa for connections between volumes
        super(position, new Vector2D(0, 0), new Vector2D(0, 0), new Vector2D(WIDTH, WIDTH), null);

        this.volume = volume;

        hasFocus = false;

        getRect().setFill(Color.ALICEBLUE);
        getRect().setStroke(Color.BLACK);
        getRect().setStrokeWidth(2);
    }

    public Volume getVolume() {
        return volume;
    }

    public static double getWidth() {
        return WIDTH;
    }

    public VolumeLink getVolumeLink() {
        return volumeLink;
    }

    public void setVolumeLink(VolumeLink volumeLink) {
        this.volumeLink = volumeLink;
    }

    public Boolean getHasFocus() {
        return hasFocus;
    }

    public void setHasFocus(Boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    public Boolean getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(Boolean isConnected) {
        this.isConnected = isConnected;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (hasFocus) {
            getRect().setFill(Color.ANTIQUEWHITE);
            getRect().setStroke(Color.CRIMSON);
            getRect().setStrokeWidth(2);
        } else {
            getRect().setFill(Color.ALICEBLUE);
            getRect().setStroke(Color.BLACK);
            getRect().setStrokeWidth(2);
        }
    }
}
