package ragdoll;

/**
 * @author Jonathan Hubermann
 * @Subject Integrative Project 420-204-RE
 * @Project Ragdoll Simulation
 */

import java.util.function.Function;

// Force class defines a force in the simulation
public class Force {
    Vector2D contact;   // contact variable holds the reference to the point of contact of the force
    Function<Vector2D,Vector2D> force;  // Function of Vector2D objects allows for recalculation of the force's value without overwriting the contact reference

    public Force (Vector2D c, Function<Vector2D,Vector2D> f) {
        this.contact = c;
        this.force = f;
    }
}
