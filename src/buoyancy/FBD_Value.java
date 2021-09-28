/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buoyancy;

import javafx.scene.paint.Color;

/**
 *
 * @author William
 */
public class FBD_Value  extends GameObject {
    
     
    public FBD_Value(Vector2D position) {
        
        super(position, new Vector2D(0, 0), new Vector2D(0, 0), 0,0);
    
}
    @Override
    public void update(double dt)
    {
        // Euler Integration
        // Update velocity
        Vector2D frameAcceleration = getAcceleration().mult(dt);
        setVelocity(getVelocity().add(frameAcceleration));

        // Update position
        setPosition(getPosition().add(getVelocity().mult(dt)));
        rectangle.setX(getPosition().getX());
        rectangle.setY(getPosition().getY());

        
        rectangle.toFront();
        
    }


}
