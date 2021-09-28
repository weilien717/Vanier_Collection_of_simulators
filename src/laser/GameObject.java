
package laser;


/**
 * Game Object Class. Initializes the position, velocity and acceleration values and methods.
 * Exists partly for integration purposes.
 * @author Haytham Hnine 1661325
 */
public class GameObject {
    private Vector2D position;
    private Vector2D velocity;
    private Vector2D acceleration;
    
    public GameObject(Vector2D position, Vector2D velocity, Vector2D acceleration)
    {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration; 
    }
    
    public void update(double dt)
    {
        // Euler Integration
        // Update velocity
        Vector2D frameAcceleration = getAcceleration().mult(dt);
        velocity = getVelocity().add(frameAcceleration);

        // Update position
        position = getPosition().add(getVelocity().mult(dt));   
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Vector2D getVelocity() {
        return velocity;
    }
    
    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }
}
