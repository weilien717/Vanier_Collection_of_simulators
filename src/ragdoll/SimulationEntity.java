package ragdoll;

/**
 * @author Jonathan Hubermann
 * @Subject Integrative Project 420-204-RE
 * @Project Ragdoll Simulation
 */

import javafx.scene.canvas.GraphicsContext;

// SimulationEntity abstract class acts as parent class to subclasses that represent entities
abstract public class SimulationEntity {
    public double rotPosition;
    public Vector2D velocity;

    abstract public void draw(GraphicsContext gc);

    abstract public void update(double timeStep);

    abstract public boolean isColliding(SimulationEntity s);
}