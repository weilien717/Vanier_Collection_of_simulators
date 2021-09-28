/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laser;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Beam Class. initializes the first Beam, and contains the basic Beam update class.
 * @author Haytham Hnine 1661325
 */
public class Beam extends GameObject {
    private Line line;
    private double wavelength;
    private double waveMult;
    
    public Beam(Vector2D position) {
        super(position, new Vector2D(0,0), new Vector2D(0,0));
        
        line = new Line();
        line.setStartX(position.getX());
        line.setStartY(position.getY());
        wavelength = 540;
        
    }
    
    public Line getLine() {
        return line;
    }
    
    public double getWavelength() {
        return wavelength;
    }
    
    public void setEnds(double x, double y) {
        line.setEndX(x);
        line.setEndY(y);
    }
    
    public void setStarts(double x, double y) {
        line.setStartX(x);
        line.setStartY(y);
    }
    
    public void setEnds(Point2D point) {
        line.setEndX(point.getX());
        line.setEndY(point.getY());
    }
    
    public void setWavelength(double wavelength) {
        this.wavelength = wavelength;
    }
    
    public void setWaveMultiplier() {
        waveMult = 0.95 + (wavelength - 380)*(0.000315);
    }
    
    //update method for the reflected beam (angle value is different)
    public void updateREFLECT(double dt, double angle, double Ypos, double refractAdjust)
    {
        // Euler Integration
        // Update velocity
        Vector2D frameAcceleration = getAcceleration().mult(dt);
        setVelocity(getVelocity().add(frameAcceleration));

        // Update position
        setPosition(getPosition().add(getVelocity().mult(dt)));       
        line.setStartX(25 + refractAdjust);
        line.setStartY(Ypos);
        line.setEndX(0);
        
        double initAngle = Math.toRadians(-angle);
        double initOppAngle = Math.PI/2 - initAngle;
        double yAdjust = (line.getStartX())*(Math.sin(initAngle)/Math.sin(initOppAngle));
        line.setEndY(line.getStartY() - yAdjust);
        

        line.toBack();      
    }
        
    //update method for if the wavelength value is default.        
    public void update(double dt, double angle, double Ypos, double refractAdjust)
    {
        // Euler Integration
        // Update velocity
        Vector2D frameAcceleration = getAcceleration().mult(dt);
        setVelocity(getVelocity().add(frameAcceleration));

        // Update position
        setPosition(getPosition().add(getVelocity().mult(dt)));       
        line.setStartX(25 + refractAdjust);
        line.setStartY(Ypos);
        line.setEndX(695 + line.getStartX());
        
        double initAngle = Math.toRadians(-angle);
        double initOppAngle = Math.PI/2 - initAngle;
        double yAdjust = 695*(Math.sin(initAngle)/Math.sin(initOppAngle));
        line.setEndY(line.getStartY() - yAdjust);
        

        line.toBack();      
    }
    
    //Update method for if the wavelength of the beam has isn't default. 
    public void update(double dt, double angle, double Ypos, double refractAdjust, double waveLengthM)
    {
        setWaveMultiplier();
        
        // Euler Integration
        // Update velocity
        Vector2D frameAcceleration = getAcceleration().mult(dt);
        setVelocity(getVelocity().add(frameAcceleration));

        // Update position
        setPosition(getPosition().add(getVelocity().mult(dt)));       
        line.setStartX(25 + refractAdjust);
        line.setStartY(Ypos);
        line.setEndX(695 + line.getStartX());
        
        double initAngle = Math.toRadians(-angle);
        double initOppAngle = Math.PI/2 - initAngle;
        double yAdjust = 695*(Math.sin(initAngle)/Math.sin(initOppAngle));
        line.setEndY(line.getStartY() - yAdjust*waveMult);
        
        line.toBack();      
    }
    
}
