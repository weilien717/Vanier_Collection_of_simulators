/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laser;

import javafx.geometry.Point2D;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

/**
 * Refracted Beam Class. Handles the operations and methods of the refracted Beam
 * @author Haytham Hnine 1661325
 */
public class RefractedBeam extends Beam {
    private Line line;
    private Line line2;
    private Line line3;
    private double wavelength;
    private double rAngle;

    
    public RefractedBeam(Vector2D position) {
        super(position);
        
        line = new Line();
        line.setStartX(position.getX());
        line.setStartY(position.getY());
        
        line2 = new Line();
        line2.setStartX(0);
        line2.setStartY(0);
        line2.setEndX(0);
        line2.setEndY(0);

        
        line3 = new Line();
        line3.setStartX(0);
        line3.setStartY(0);
        line3.setEndX(0);
        line3.setEndY(0);
    }
    
    //Getter methods
    public void setStarts(Vector2D pos) {
        line.setStartX(pos.getX());
        line.setStartY(pos.getY());
    }
    
    public Line getLine2() {
        return line2;
    }
 
    public Line getLine3() {
        return line3;
    }
        
    public double getRefrAngle(){
        return rAngle;
    }
    
    //Resets the second and third lines to their initial 0,0 posisions
    public void line2Reset() {
        line2.setStartX(0);
        line2.setStartY(0);
        line2.setEndX(0);
        line2.setEndY(0);
    }
    
    public void line3Reset() {
        line3.setStartX(0);
        line3.setStartY(0);
        line3.setStartX(0);
        line3.setEndY(0);
    }
    
    public void update(double dt, double angle, Point2D StartPoint, double ratio, double size, double Ypos, double Xpos)
    {
        //Calculating the initial refracted angle
        double initAngle = Math.abs(angle);
        double rInitAngle = Math.toRadians(initAngle);
        rAngle = Math.toDegrees(Math.asin(ratio*Math.sin(rInitAngle)));
        
        // Euler Integration
        // Update velocity
        Vector2D frameAcceleration = getAcceleration().mult(dt);
        setVelocity(getVelocity().add(frameAcceleration));

        // Update position
        setPosition(getPosition().add(getVelocity().mult(dt)));
        line.setStartX(StartPoint.getX());
        line.setStartY(StartPoint.getY());
        
        //Detect if the prism is located above, below or in front of the pointer.
        if(angle == 0) {
           setEnds(line.getStartX() + size, line.getStartY()); 
        }
        
        else if(angle < 0){          
            squareUpRefract(angle, StartPoint, ratio, size, Ypos, Xpos);
        }
        
        else {
            squareDownRefract(angle, StartPoint, ratio, size, Ypos, Xpos);
        }
               
        
        line.toBack();
        line2.toBack();
        line3.toBack();
    }
    
    public void squareUpRefract(double angle, Point2D StartPoint, double ratio, double size, double Ypos, double Xpos) {
        //Calculating initial and opposite angle
        double initAngle = Math.abs(angle);
        double oppInitAngle = 90 - initAngle;
        
        double rInitAngle = Math.toRadians(initAngle);
        double rOppInitAngle = Math.toRadians(oppInitAngle);
        
        //Refraction from the leftmost side of the prism
            if(line.getStartX() == Xpos) {
                    
                double initAngleRad = Math.toRadians(initAngle);
                double initARS = Math.sin(initAngleRad);
                double refrAngleSin = ratio*initARS;
        
                double refrAngle = Math.asin(refrAngleSin);
        
                double refrOppAngle = Math.toRadians(90 - Math.toDegrees(refrAngle));
                
                    double opp = line.getStartY() - Ypos;
                    double oppRatio = opp/Math.sin(refrAngle);
                    double or2 = Math.abs(oppRatio*Math.sin(refrOppAngle));
                    
                    //if the refraction makes the refracted beam cross the prism without touching any other sides
                    if(or2 > size ) {
                
                        double smallerOpp = or2 - size;
                        double sOR = smallerOpp/Math.sin(refrOppAngle);
                        double sOR2 = sOR*Math.sin(refrAngle);
                        
                        //Pointing up
                        if(angle < 0 ) {
                            line2.setStartX(0);
                            line2.setStartY(0);
                            line2.setEndX(0);
                            line2.setEndY(0);
                            setEnds(line.getStartX() + size, Ypos+ sOR2);
                        }
                                
                    }
                    
                    //Internal Refraction
                    else {
                        if(angle < 0) {
                            line2.setStartX(Xpos + or2);
                            line2.setStartY(Ypos);
                            line2.setEndX(Xpos + size);
                        
                            double extraXlength = line2.getEndX() - line2.getStartX();
                            double SinRatio = Math.sin(refrAngle)/Math.sin(refrOppAngle);
                            double extraYlength = extraXlength*SinRatio;
                        
                            line2.setEndY(Ypos + extraYlength);
                        
                            setEnds(line.getStartX() + or2, Ypos);
                        }
                    }
                    
                    line3.setStartX(0);
                    line3.setStartY(0);
                    line3.setEndX(0);
                    line3.setEndY(0);
                                         
            }
            
            
            //Refraction from the top or bottom sides of the prism
            else {
                double initAngleRad = Math.toRadians(90 - initAngle);
                double initARS = Math.sin(initAngleRad);
                double refrAngleSin = ratio*initARS;
                
                double refrAngle = Math.asin(refrAngleSin);
                
                double refrOppAngle = Math.toRadians(90 - Math.toDegrees(refrAngle));
                
                double opp = (Xpos + size) - line.getStartX();
                double angleRatio = Math.sin(refrOppAngle)/Math.sin(refrAngle);
                double side = angleRatio*opp;
                
                //if the refraction makes the refracted beam cross the prism without touching any other sides
                if(side > size) {
                
                    double smallerSide = side - size;
                    double angleRatio2 = Math.sin(refrAngle)/Math.sin(refrOppAngle);
                    double trueX = angleRatio2*smallerSide;
                    
                    double tx = (Xpos + size) - trueX;
                    
                    line2.setStartX(0);
                    line2.setStartY(0);
                    line2.setEndX(0);
                    line2.setEndY(0);
                    
                    line3.setStartX(0);
                    line3.setStartY(0);
                    line3.setEndX(0);
                    line3.setEndY(0);
                
                    setEnds(tx, Ypos);
                }
                
                //Internal Refraction
                else {
                    double newopp = size - side;
                    double sRatio = Math.sin(refrAngle)/Math.sin(refrOppAngle);
                    double trueX = sRatio*newopp;
                    double txa = size - trueX;
                    
                    setEnds(Xpos + size, line.getStartY() - side);
                    line2.setStartX(Xpos + size);
                    line2.setStartY(Ypos + newopp);
                    line2.setEndX(Xpos + txa);
                    line2.setEndY(Ypos);
                    
                    
                    
                    line3.setStartX(line2.getEndX());
                    line3.setStartY(line2.getEndY());
                    line3.setEndX(0);
                    line3.setEndY(Ypos - (line3.getStartX()*(Math.sin(rInitAngle)/Math.sin(rOppInitAngle))));
                }
                
            }
        
    }
    
    public void squareDownRefract(double angle, Point2D StartPoint, double ratio, double size, double Ypos, double Xpos) {
        //Calculating initial and opposite angle
        double initAngle = Math.abs(angle);
        double oppInitAngle = 90 - initAngle;
        
        double rInitAngle = Math.toRadians(initAngle);
        double rOppInitAngle = Math.toRadians(oppInitAngle); 
        
        //Refraction from the leftmost side of the prism
        if(line.getStartX() == Xpos) { 
            double refrAngle = Math.asin(ratio*Math.sin(rInitAngle));
            double refrOppAngle = Math.PI/2 - refrAngle;
            
            double opp = size - (line.getStartY() - Ypos);
            double line1Side = opp*(Math.sin(refrOppAngle)/Math.sin(refrAngle));
            
            //Refraction makes the beam cross the prism without touching any other sides
            if(line1Side > size && line.getEndY() < Ypos + size) {
                double smallerOpp = line1Side - size;
                double smallerLine1Side = smallerOpp*(Math.sin(refrAngle)/Math.sin(refrOppAngle));
                line2Reset();
                setEnds(Xpos + size, Ypos + size - smallerLine1Side);
                
                    line3.setStartX(0);
                    line3.setStartY(0);
                    line3.setEndX(0);
                    line3.setEndY(0);
            }
            
            //internal refraction
            else {
                setEnds(Xpos + line1Side, Ypos + size);
                line2.setStartX(Xpos + line1Side);
                line2.setStartY(Ypos + size);                
                line2.setEndX(Xpos + size);
                
                double line2Adj = size - line1Side;
                double line2Opp = line2Adj*(Math.sin(refrAngle)/Math.sin(refrOppAngle));
                line2.setEndY(Ypos + size - line2Opp);
                
                                    line3.setStartX(0);
                    line3.setStartY(0);
                    line3.setEndX(0);
                    line3.setEndY(0);
            }
        }
        
        //Side Refraction
        else {
                double initAngleRad = Math.toRadians(90 - initAngle);
                double initARS = Math.sin(initAngleRad);
                double refrAngleSin = ratio*initARS;
                
                double refrAngle = Math.asin(refrAngleSin);
                
                double refrOppAngle = Math.toRadians(90 - Math.toDegrees(refrAngle));
                
                double opp = (Xpos + size) - line.getStartX();
                double angleRatio = Math.sin(refrOppAngle)/Math.sin(refrAngle);
                double side = angleRatio*opp;
                
                //if the refraction makes the refracted beam cross the prism without touching any other sides
                if(side > size) {
                
                    double smallerSide = side - size;
                    double angleRatio2 = Math.sin(refrAngle)/Math.sin(refrOppAngle);
                    double trueX = angleRatio2*smallerSide;
                    
                    double tx = (Xpos + size) - trueX;
                    
                    line2.setStartX(0);
                    line2.setStartY(0);
                    line2.setEndX(0);
                    line2.setEndY(0);
                    
                    line3.setStartX(0);
                    line3.setStartY(0);
                    line3.setEndX(0);
                    line3.setEndY(0);
                
                    setEnds(tx, Ypos + size);
                }
                
                //Internal refraction
                else {
                    double newopp = size - side;
                    double sRatio = Math.sin(refrAngle)/Math.sin(refrOppAngle);
                    double trueX = sRatio*newopp;
                    double txa = size - trueX;
                    
                    setEnds(Xpos + size, line.getStartY() + side);
                    line2.setStartX(Xpos + size);
                    line2.setStartY(line.getStartY() + side);
                    line2.setEndX(Xpos + txa);
                    line2.setEndY(Ypos + size);
                    
                    
                    
                    line3.setStartX(line2.getEndX());
                    line3.setStartY(line2.getEndY());
                    line3.setEndX(0);
                    line3.setEndY(line3.getStartY() + (line3.getStartX()*(Math.sin(rInitAngle)/Math.sin(rOppInitAngle))));
                }
                
            }
        
    }
}
