package ragdoll;

/**
 * @author Jonathan Hubermann
 * @Subject Integrative Project 420-204-RE
 * @Project Ragdoll Simulation
 */

// Vector2D class to define vectors and points, and use them in calculations
public class Vector2D {
    protected double x;
    protected double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D v) {
        this.x = v.x;
        this.y = v.y;
    }

    // Method to compare value equality of two Vector2D objects
    public boolean equal(Vector2D v) {
        if (this.x == v.x && this.y == v.y) {
            return true;
        } else {
            return false;
        }
    }

    // Operation methods

    // Method to add two vectors and return new Vector2D
    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    // Method to find difference between two vectors and return new Vector2D
    public Vector2D sub(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    // Method to multiply a vector by scalar and return new Vector2D
    public Vector2D mult(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }

    // Method to calculate coordinates of point rotated about another point by an angle
    public Vector2D rotate(Vector2D other, double angle) {
        double x = this.x - other.x;
        double y = this.y - other.y;

        double x_prime = other.x + ((x * Math.cos(angle)) - (y * Math.sin(angle)));
        double y_prime = other.y + ((x * Math.sin(angle)) + (y * Math.cos(angle)));

        return new Vector2D(x_prime, y_prime);
    }

    // Method to calculate cross product between two vectors
    public double cross(Vector2D other) {
        return this.x * other.y - this.y * other.x;
    }

    // Method to add two vectors and return the same instance variable
    // This is used to move a SimulationEntity without overwriting the object reference used elsewhere
    public void addition(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
    }

    // Method to calculate coordinates of point rotated about another point by an angle and return the same instance variable
    // This is used to rotate a SimulationEntity without overwriting the object reference used elsewhere
    public void rotation(Vector2D other, double angle) {
        double x = this.x - other.x;
        double y = this.y - other.y;

        double x_prime = other.x + ((x * Math.cos(angle)) - (y * Math.sin(angle)));
        double y_prime = other.y + ((x * Math.sin(angle)) + (y * Math.cos(angle)));

        this.x = x_prime;
        this.y = y_prime;
    }

    // Method to calculate vector magnitude
    public double magnitude() {
        return Math.sqrt(this.dot(this));
    }

    // Method to calculate dot product between two vectors
    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }

    // Method to calculate the unit vector of a vector
    public Vector2D norm() {
        return this.mult(1.0 / this.magnitude());
    }

    // Overriding the default toString() method
    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "]";
    }
}
