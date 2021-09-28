/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laser;

import javafx.scene.shape.Rectangle;

/**
 * Laser Class. Initializes the Rectangle shape of the Laser pointer and its methods. 
 * @author Haytham Hnine
 */
public class Laser extends GameObject{
    private Rectangle rectangle;
    
    public Laser()
    {      
        super(new Vector2D(8, 200), new Vector2D(0,0), new Vector2D(0,0));     
        rectangle = new Rectangle(84, 34);
        rectangle.setX(getPosition().getX());
        rectangle.setY(getPosition().getY());
    }
    
    public Rectangle getRectangle()
    {
        return rectangle;
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
