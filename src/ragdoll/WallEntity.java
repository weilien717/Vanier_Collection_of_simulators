package ragdoll;

/**
 * @author Jonathan Hubermann
 * @Subject Integrative Project 420-204-RE
 * @Project Ragdoll Simulation
 */

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;


// WallEntity class to define a wall that represents the boundaries of inside the bus
public class WallEntity extends SimulationEntity {
    protected Vector2D start;   // Vector of starting point
    protected Vector2D end; // Vector of ending point
    protected double rotPosition; // Variable to hold rotational position of wall for quicker AABB collision detection


    public WallEntity(Vector2D b, Vector2D e) {
        this.start = b;
        this.end = e;
        this.rotPosition = 0;
    }
    // Method returns list of wall vertices
    public ArrayList<Vector2D> vertices() {
        ArrayList<Vector2D> temp = new ArrayList<>();
        temp.add(this.start);
        temp.add(this.end);
        return temp;
    }

    // Overridden from super class but serves no purpose for this class
    @Override
    public void draw(GraphicsContext gc) {}

    // Overridden from super class but serves no purpose for this class
    @Override
    public void update(double timeStep) {

    }

    // Calls the AABB collision detection method when called
    @Override
    public boolean isColliding(SimulationEntity se) {
        return SAT.isCollidingAABB(this, se);

    }
}









