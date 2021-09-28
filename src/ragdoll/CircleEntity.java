package ragdoll;

/**
 * @author Jonathan Hubermann
 * @Subject Integrative Project 420-204-RE
 * @Project Ragdoll Simulation
 */

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

// CircleEntity class defines a circle object ragdoll
public class CircleEntity extends SimulationEntity {
    protected double radius;
    protected Vector2D position;
    protected Vector2D acceleration;
    protected double rotPosition;
    protected double rotVelocity;
    protected double rotAcceleration;
    protected double mass;
    protected double inertia;
    protected ArrayList<Force> extForces;

    public CircleEntity(double x, double y, double r, double m) {
        this.radius = r;
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0.0,0.0);
        this.acceleration = new Vector2D(0.0,0.0);
        this.rotPosition = 0.0;
        this.rotVelocity = 0.0;
        this.rotAcceleration = 0.0;
        this.mass = m;
        this.inertia = this.mass * (this.radius*this.radius) / 2;
        this.extForces = new ArrayList<>();
    }

    // move() method to displace the circle object
    public CircleEntity move(Vector2D other) {
        this.position.addition(other);
        return this;
    }

    // rotate() method to rotate the circle object around its center
    public CircleEntity rotate(double angle) {
        this.rotPosition += angle;
        this.position.rotation(position, angle);
        return this;
    }

    // draw() method to render the CircleEntity when called
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.ORANGE);
        gc.setFill(Color.ORANGE);
        gc.save();
        gc.translate(this.position.x, this.position.y);
        gc.rotate(Math.toDegrees(this.rotPosition));
        gc.fillOval(0-this.radius, 0-this.radius, this.radius*2, this.radius*2);
        gc.restore();
    }

    // overridden parent class method that first checks if a SAT collision occurs
    public boolean isColliding(SimulationEntity se) {
        return SAT.isCollidingSAT(this, se);
    }

    // update() method handles circle objects' kinematics, translational and rotational
    public void update(double timeStep) {

        Vector2D force = new Vector2D(0, 0);
        double torque = 0;

        // Starts Velocity Verlet Integration by performing the translational displacement
        Vector2D dr = this.velocity.mult(timeStep).add(this.acceleration.mult(0.5 * timeStep * timeStep));
        this.move(dr.mult(100));

        double rr = this.rotVelocity * timeStep + this.rotAcceleration * 0.5 * timeStep * timeStep;
        this.rotate(rr);

        force = force.add(this.velocity.mult(-1));  // adds a damping force to the net force to simulate realistic loss of energy

        // loops through the object's external forces and calculates the torque they produce, adding each to the net force or net torque values
        for (Force eF : this.extForces) {
            Vector2D r = this.position.sub(eF.contact);
            double rxf = r.cross(eF.force.apply(eF.contact));
            force = force.add(eF.force.apply(eF.contact));
            torque += -1 * rxf;
            //System.out.println(eF.force.apply(eF.contact).toString());
        }

        // after addition of all other forces, the Velocity Verlet Integration completes by updating the new acceleration
        Vector2D newAcceleration = force.mult(1/this.mass);
        Vector2D dv = this.acceleration.add(newAcceleration).mult(0.5 * timeStep);
        this.velocity = this.velocity.add(dv);

        torque += this.rotVelocity * -2;    // angular damping added to torque to simulate realistic torque loss
        double newRotAcceleration = torque / this.inertia;
        double rv = this.rotAcceleration + newRotAcceleration * 0.5 * timeStep;
        this.rotVelocity += rv;
    }
}