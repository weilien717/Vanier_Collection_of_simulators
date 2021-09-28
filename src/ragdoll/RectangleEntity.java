package ragdoll;

/**
 * @author Jonathan Hubermann
 * @Subject Integrative Project 420-204-RE
 * @Project Ragdoll Simulation
 */

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

// RectangleEntity class defines a rectangle object ragdoll
public class RectangleEntity extends SimulationEntity {
    protected double width;
    protected double height;
    protected Vector2D topLeft;
    protected Vector2D topRight;
    protected Vector2D bottomRight;
    protected Vector2D bottomLeft;
    protected Vector2D center;
    protected Vector2D acceleration;
    protected double rotPosition;
    protected double rotVelocity;
    protected double rotAcceleration;
    protected double mass;
    protected double inertia;
    protected ArrayList<Force> extForces;

    public RectangleEntity(double x, double y, double w, double h, double m) {
        this.width = w;
        this.height = h;
        this.topLeft = new Vector2D(x, y);
        this.topRight = new Vector2D(x + w, y);
        this.bottomRight = new Vector2D(x + w, y + h);
        this.bottomLeft = new Vector2D(x, y + h);
        this.center = this.center();
        this.velocity = new Vector2D(0.0, 0.0);
        this.acceleration = new Vector2D(0.0, 0.0);
        this.rotPosition = 0.0;
        this.rotVelocity = 0.0;
        this.rotAcceleration = 0.0;
        this.mass = m;
        this.inertia = this.mass * (this.height * this.height + this.width * this.width) / 12000;
        this.extForces = new ArrayList<>();
    }

    // center() method to find center of rectangle
    public Vector2D center() {
        Vector2D diagonal = this.bottomRight.sub(this.topLeft);
        Vector2D midpoint = this.topLeft.add(diagonal.mult(0.5));
        return midpoint;
    }

    // move() method to displace the rectangle object
    public RectangleEntity move(Vector2D other) {
        this.topLeft.addition(other);
        this.topRight.addition(other);
        this.bottomRight.addition(other);
        this.bottomLeft.addition(other);
        this.center.addition(other);
        return this;
    }

    // rotate() method to rotate the rectangle object around its center
    public RectangleEntity rotate(double angle) {
        this.rotPosition += angle;
        Vector2D center = this.center();

        this.topLeft.rotation(center, angle);
        this.topRight.rotation(center, angle);
        this.bottomRight.rotation(center, angle);
        this.bottomLeft.rotation(center, angle);
        this.center.rotation(center, angle);
        return this;
    }

    // vertex() method returns a vertex, method is used in collision detection
    public Vector2D vertex(int id) {
        if (id == 0)
        {
            return this.topLeft;
        }
        else if (id == 1)
        {
            return this.topRight;
        }
        else if (id == 2)
        {
            return this.bottomRight;
        }
        else if (id == 3)
        {
            return this.bottomLeft;
        }
        return null;
    }

    // vertices() method returns list of all the rectangle's vertices
    public ArrayList<Vector2D> vertices() {
        ArrayList<Vector2D> temp = new ArrayList<>();
        temp.add(this.topLeft);
        temp.add(this.topRight);
        temp.add(this.bottomRight);
        temp.add(this.bottomLeft);
        return temp;
    }

    // overridden parent class method that first checks if an AABB collision occurs
    @Override
    public boolean isColliding(SimulationEntity se) {
        return SAT.isCollidingAABB(this, se);
    }

    // draw() method to render the RectangleEntity when called
    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLUE);
        gc.setFill(Color.BLUE);
        gc.save();
        gc.translate(this.topLeft.x, this.topLeft.y);
        gc.rotate(Math.toDegrees(this.rotPosition));
        gc.fillRect(0, 0, this.width, this.height);
        gc.restore();
    }

    // update() method handles rectangle objects' kinematics, translational and rotational
    @Override
    public void update(double timeStep) {

        Vector2D force = new Vector2D(0, 0);
        double torque = 0;

        // Starts Velocity Verlet Integration by performing the translational displacement
        Vector2D dr = this.velocity.mult(timeStep).add(this.acceleration.mult(0.5 * timeStep * timeStep));
        this.move(dr.mult(100));


        force = force.add(this.velocity.mult(-1));  // adds a damping force to the net force to simulate realistic loss of energy

        // loops through the object's external forces and calculates the torque they produce, adding each to the net force or net torque values
        for (Force eF : this.extForces) {
            Vector2D r = this.center().sub(eF.contact);
            double rxf = r.cross(eF.force.apply(eF.contact));
            force = force.add(eF.force.apply(eF.contact));
            torque += -1 * rxf;
        }

        // after addition of all other forces, the Velocity Verlet Integration completes by updating the new acceleration
        Vector2D newAcceleration = force.mult(1/this.mass);
        Vector2D dv = this.acceleration.add(newAcceleration).mult(0.5 * timeStep);
        this.velocity = this.velocity.add(dv);

        torque += this.rotVelocity * -7; // angular damping added to torque to simulate realistic torque loss
        this.rotAcceleration = torque / this.inertia;
        this.rotVelocity += this.rotAcceleration * timeStep;
        double deltaRotPosition = this.rotVelocity * timeStep;
        this.rotate(deltaRotPosition); // rotates the rectangle by calculated value deltaRotPosition
    }
}