package buoyancy;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


 public class GameObject {
    //protected Circle circle;
    private Vector2D position;
    private Vector2D velocity;
    private Vector2D acceleration;
    protected Rectangle rectangle;
    
    public GameObject(Vector2D position, Vector2D velocity, Vector2D acceleration, double width, double height)
    {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration; 
        
        rectangle = new Rectangle(0.0, 0.0 ,width, height);
        
        /*circle = new Circle(0.0, 0.0, radius);
        circle.setCenterX(position.getX());
        circle.setCenterY(position.getY());*/
    }
    
    public Rectangle getRectangle(){
        return rectangle;
    }
    
    /*public Circle getCircle()
    {
        return circle;
    }*/
    
    public void update(double dt)
    {
       
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
    public void setAcceleration(Vector2D acceleration){
        this.acceleration = acceleration;
    }
}