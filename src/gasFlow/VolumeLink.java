package gasflow;

import javafx.scene.shape.Line;

public class VolumeLink {

    private final GasSimPane gsp;

    private final static double size = 0.35;

    private Boolean isActivated;

    private VolNode startNode = null;
    private VolNode endNode = null;

    private Line[] lines = new Line[3];

    public VolumeLink(VolNode start, GasSimPane gsp) {
        //constructor
        startNode = start;

        isActivated = false;

        if (startNode.equals(startNode.getVolume().getVolNodes()[0])) {
            lines[0] = new Line(startNode.getPosition().getX(), startNode.getPosition().getY() + VolNode.getWidth() / 2, startNode.getPosition().getX() - 10, startNode.getPosition().getY() + VolNode.getWidth() / 2);
        } else if (startNode.equals(startNode.getVolume().getVolNodes()[1])) {
            lines[0] = new Line(startNode.getPosition().getX() + VolNode.getWidth() / 2, startNode.getPosition().getY(), startNode.getPosition().getX() + VolNode.getWidth() / 2, startNode.getPosition().getY() - 10);
        } else if (startNode.equals(startNode.getVolume().getVolNodes()[2])) {
            lines[0] = new Line(startNode.getPosition().getX() + VolNode.getWidth(), startNode.getPosition().getY() + VolNode.getWidth() / 2, startNode.getPosition().getX() + VolNode.getWidth() + 10, startNode.getPosition().getY() + VolNode.getWidth() / 2);
        } else if (startNode.equals(startNode.getVolume().getVolNodes()[3])) {
            lines[0] = new Line(startNode.getPosition().getX() + VolNode.getWidth() / 2, startNode.getPosition().getY() + VolNode.getWidth(), startNode.getPosition().getX() + VolNode.getWidth() / 2, startNode.getPosition().getY() + VolNode.getWidth() + 10);
        }

        this.gsp = gsp;
    }

    public static double getSize() {
        return size;
    }

    public void setEndPosition(Vector2D position) {
        //sets end position for displayed line
        lines[1] = new Line(lines[0].getEndX(), lines[0].getEndY(), position.getX(), position.getY());
    }

    public void setEndNode(VolNode end) {
        //sets the node that is stored as the end node
        endNode = end;

        if (endNode.getVolumeLink() != null) {
            gsp.removeVolLink(endNode.getVolumeLink());
        }

        if (endNode.equals(endNode.getVolume().getVolNodes()[0])) {
            lines[2] = new Line(endNode.getPosition().getX(), endNode.getPosition().getY() + VolNode.getWidth() / 2, endNode.getPosition().getX() - 10, endNode.getPosition().getY() + VolNode.getWidth() / 2);
        } else if (endNode.equals(endNode.getVolume().getVolNodes()[1])) {
            lines[2] = new Line(endNode.getPosition().getX() + VolNode.getWidth() / 2, endNode.getPosition().getY(), endNode.getPosition().getX() + VolNode.getWidth() / 2, endNode.getPosition().getY() - 10);
        } else if (endNode.equals(endNode.getVolume().getVolNodes()[2])) {
            lines[2] = new Line(endNode.getPosition().getX() + VolNode.getWidth(), endNode.getPosition().getY() + VolNode.getWidth() / 2, endNode.getPosition().getX() + VolNode.getWidth() + 10, endNode.getPosition().getY() + VolNode.getWidth() / 2);
        } else if (endNode.equals(endNode.getVolume().getVolNodes()[3])) {
            lines[2] = new Line(endNode.getPosition().getX() + VolNode.getWidth() / 2, endNode.getPosition().getY() + VolNode.getWidth(), endNode.getPosition().getX() + VolNode.getWidth() / 2, endNode.getPosition().getY() + VolNode.getWidth() + 10);
        }

        lines[1].setEndX(lines[2].getEndX());
        lines[1].setEndY(lines[2].getEndY());
    }

    public double getDistance(double x, double y) {
        //return minimum distance between lines and point (x,y)
        double min = 999999999;
        for (Line l : lines) {
            if (l != null) {
                Vector2D v = new Vector2D(l.getStartX(), l.getStartY());
                Vector2D w = new Vector2D(l.getEndX(), l.getEndY());
                Vector2D p = new Vector2D(x, y);
                double length = w.sub(v).magnitude();
                double lengthSquared = length * length;

                if (lengthSquared == 0) {
                    return p.sub(v).magnitude();
                }

                double t = Math.max(0, Math.min(1, p.sub(v).dot(w.sub(v)) / lengthSquared));
                Vector2D projection = v.add(w.sub(v).mult(t));
                double distance = p.sub(projection).magnitude();
                if (distance < min) {
                    min = distance;
                }
            }
        }
        return min;
    }

    public Line[] getLines() {
        //returns the lines to be displayed
        return lines;
    }

    public VolNode getStartNode() {
        return startNode;
    }

    public VolNode getEndNode() {
        return endNode;
    }

    public void deleteVolLinkLines() {
        //deletes the Volume link and lines that are displayed
        for (Line l : lines) {
            gsp.removeFromPane(l);
        }
        startNode.setVolumeLink(null);
        endNode.setVolumeLink(null);
    }

    public Boolean getIsActivated() {
        return isActivated;
    }

    public void toggleIsActivated() {
        isActivated = !isActivated;
    }

    public void update(double dt) {
        if (isActivated) {
            for (Line l : lines) {
                if (l != null) {
                    l.setStrokeWidth(8);
                }
            }
        } else {
            for (Line l : lines) {
                if (l != null) {
                    l.setStrokeWidth(1);
                }
            }
        }
    }
}
