
package laser;

import javafx.scene.shape.Rectangle;

/**
 * PrismSquare Class. Initializes the rectangle shape of the prism, as well as its index and methods.
 * @author Haytham Hnine 1661325
 */
public class PrismSquare extends GameObject{
    private Rectangle rectangle;
    private double index;
    
    public PrismSquare(Vector2D position, double size){
        super(position, new Vector2D(0, 0), new Vector2D(0,0));
        
        rectangle = new Rectangle(size, size);
        rectangle.setX(position.getX());
        rectangle.setY(position.getY());
        index = 1;
    }
    
    public Rectangle getRectangle() {
        return rectangle;
    }
    
    public double getIndex() {
        return index;
    }
    
    public void setIndex(double i) {
        index = i;
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
