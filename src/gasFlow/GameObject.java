package gasflow;

import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class GameObject {

    private Rectangle rectangle;
    private Vector2D position;
    private Vector2D velocity;
    private Vector2D acceleration;
    private Vector2D size;

    public GameObject(Vector2D position, Vector2D velocity, Vector2D acceleration, Vector2D size, ImagePattern img) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.size = size;

        rectangle = new Rectangle(this.size.getX(), this.size.getY());
        rectangle.setX(position.getX());
        rectangle.setY(position.getY());

        rectangle.setFill(img);
    }

    public Rectangle getRect() {
        return rectangle;
    }

    public void update(double dt) {
        // Euler Integration
        // Update velocity
        Vector2D frameAcceleration = getAcceleration().mult(dt);
        velocity = getVelocity().add(frameAcceleration);

        // Update position
        position = getPosition().add(getVelocity().mult(dt));
        rectangle.setX(position.getX());
        rectangle.setY(position.getY());

        //update size apparent
        rectangle.setWidth(getSize().getX());
        rectangle.setHeight(getSize().getY());
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
        rectangle.setX(position.getX());
        rectangle.setY(position.getY());
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }

    public Vector2D getSize() {
        return size;
    }

    protected void setSize(Vector2D size) {
        this.size = size;
        rectangle.setWidth(getSize().getX());
        rectangle.setHeight(getSize().getY());
    }

    protected void setImg(ImagePattern img) {
        this.rectangle.setFill(img);
    }
}
