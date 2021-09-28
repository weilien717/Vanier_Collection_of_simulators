package ragdoll;

/**
 * @author Jonathan Hubermann
 * @Subject Integrative Project 420-204-RE
 * @Project Ragdoll Simulation
 */

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// SpringEntity class defines a spring that can be attached to another entity
public class SpringEntity extends SimulationEntity {
    protected Vector2D origin;  // Vector of origin point
    protected Vector2D destination; //Vector of destination point (destination represents a location on another entity)

    public SpringEntity(double x, double y, Vector2D d) {
        this.origin = new Vector2D(x, y);
        this.destination = d;
    }

    // draw() method to render the SpringEntity when called
    public void draw(GraphicsContext gc){
        gc.setStroke(Color.RED);
        gc.beginPath();
        gc.moveTo(this.origin.x, this.origin.y);
        gc.lineTo(this.destination.x, this.destination.y);
        gc.stroke();
        gc.closePath();
    }

    public void update(double timeStep) {}

    // Method returns false since springs in simulation are fixed and do not move from origin
    public boolean isColliding(SimulationEntity s) {
        return false;
    }
}
