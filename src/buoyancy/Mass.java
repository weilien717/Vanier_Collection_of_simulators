package buoyancy;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Mass extends GameObject {
    
     
    public Mass(Vector2D position, String material) {
        
        super(position, new Vector2D(0, 0), new Vector2D(0, 0), 70,70);
        
        // Set the image based on the material chosen 
        
        if(material.equals("CottonWood (400kg/m^3)")){
            rectangle.setFill(AssetManager.getCottonWoodBoxImage());
        }
        else if(material.equals("Ice Cube (920kg/m^3)")){
            rectangle.setFill(AssetManager.getIceCubeImage()); 
        }
        else if(material.equals("EbonyWood (1200kg/m^3)")){
            rectangle.setFill(AssetManager.getEbonyWoodBoxImage());
        }
        else if(material.equals( "Brick (2000kg/m^3)")){
            rectangle.setFill(AssetManager.getBrickImage());
        }
   
        
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


