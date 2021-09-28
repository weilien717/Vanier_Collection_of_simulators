package ragdoll;

/**
 * @author Jonathan Hubermann
 * @Subject Integrative Project 420-204-RE
 * @Project Ragdoll Simulation
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// SAT class defines all collision detection methods and functionality
public class SAT {

    public static Vector2D collisionPoint; // collisionPoint defines the point of collision found through SAT algorithm
    public static boolean AABB = false; // AABB defines if a collision was between Axis-Aligned-Bounding-Boxes or not


    public static Vector2D getCollisionPoint() {
        return collisionPoint;
    }

    // isCollidingSAT() method to detect collisions through the Separating Axis Theorem
    public static boolean isCollidingSAT(SimulationEntity s1, SimulationEntity s2) {
        if (s1 instanceof WallEntity && s2 instanceof RectangleEntity) { // in the case of a non-AABB collision between a wall and a rectangle
            WallEntity tempWall = (WallEntity) s1;
            RectangleEntity r = (RectangleEntity) s2;
            RectangleEntity w;
            if (tempWall.end.sub(tempWall.start).magnitude() == 410 - 95) { // checks what wall is involved and creates a temporary RectangleEntity around it
                w = new RectangleEntity(tempWall.start.x, tempWall.start.y, 1, tempWall.end.sub(tempWall.start).magnitude(), 1);
            } else {
                w = new RectangleEntity(tempWall.start.x, tempWall.start.y, tempWall.end.sub(tempWall.start).magnitude(), 1, 1);
            }

            ArrayList<Vector2D> testVectors = new ArrayList<>(); // testVectors holds all axes to be projected
            testVectors.add(r.topRight.sub(r.topLeft));
            testVectors.add(r.bottomRight.sub(r.topRight));
            testVectors.add(w.topRight.sub(w.topLeft));
            testVectors.add(w.bottomRight.sub(w.topRight));

            ArrayList<ArrayList<Vector2D>> rInvolvedVertices = new ArrayList<>();
            ArrayList<ArrayList<Vector2D>> wInvolvedVertices = new ArrayList<>();

            for (int i = 0; i < 4; i++) { // these nested for loops check if a gap exists between each projection's point and adds any vertices involved in the collision to a list
                rInvolvedVertices.add(new ArrayList<>());
                rInvolvedVertices.add(new ArrayList<>());
                rInvolvedVertices.add(new ArrayList<>());
                rInvolvedVertices.add(new ArrayList<>());
                wInvolvedVertices.add(new ArrayList<>());
                wInvolvedVertices.add(new ArrayList<>());
                wInvolvedVertices.add(new ArrayList<>());
                wInvolvedVertices.add(new ArrayList<>());
                ArrayList<Double> thisProj = new ArrayList<>();
                ArrayList<Double> otherProj = new ArrayList<>();

                for (int j = 0; j < 4; j++) {
                    thisProj.add(testVectors.get(i).dot(r.vertex(j)));
                    otherProj.add(testVectors.get(i).dot(w.vertex(j)));
                }

                for (int j = 0; j < otherProj.size(); j++) {
                    if (otherProj.get(j) > Collections.min(thisProj) && otherProj.get(j) < Collections.max(thisProj)) {
                        wInvolvedVertices.get(i).add(w.vertex(j));
                    }
                }

                for (int j = 0; j < thisProj.size(); j++) {
                    if (thisProj.get(j) > Collections.min(otherProj) && thisProj.get(j) < Collections.max(otherProj)) {
                        rInvolvedVertices.get(i).add(r.vertex(j));
                    }
                }
            }

            ArrayList<Vector2D> rInvolvedVerticesNew;
            ArrayList<Vector2D> wInvolvedVerticesNew;

            rInvolvedVerticesNew = filterIntersection(filterIntersection(rInvolvedVertices.get(0), rInvolvedVertices.get(1)), filterIntersection(rInvolvedVertices.get(2), rInvolvedVertices.get(3))); // filters through all involved vertices and adds confirmed vertices to new list
            wInvolvedVerticesNew = filterIntersection(filterIntersection(wInvolvedVertices.get(0), wInvolvedVertices.get(1)), filterIntersection(wInvolvedVertices.get(2), wInvolvedVertices.get(3)));

            if (rInvolvedVerticesNew.size() == 1 && wInvolvedVerticesNew.size() == 2) { // if statements that return the collision point depending on how many vertices of each object are involved
                collisionPoint = rInvolvedVerticesNew.get(0);

            } else if (wInvolvedVerticesNew.size() == 1 && rInvolvedVerticesNew.size() == 2) {
                collisionPoint = wInvolvedVerticesNew.get(0);

            } else if (rInvolvedVerticesNew.size() == 1 && wInvolvedVerticesNew.size() == 1) {
                collisionPoint = rInvolvedVerticesNew.get(0);

            } else if (rInvolvedVerticesNew.size() == 1 && wInvolvedVerticesNew.size() == 0) {
                collisionPoint = rInvolvedVerticesNew.get(0);

            } else if (rInvolvedVerticesNew.size() == 0 && wInvolvedVerticesNew.size() == 1) {
                collisionPoint = wInvolvedVerticesNew.get(0);

            } else if (rInvolvedVerticesNew.size() == 0 && wInvolvedVerticesNew.size() == 0) {
                return false;

            } else {
                System.out.println("Unknown collision profile"); // executes if the collision point cannot be determined (ex: AABB collisions)
                for (Vector2D v : rInvolvedVerticesNew) {
                    System.out.println(v);
                }
                for (Vector2D v : wInvolvedVerticesNew) {
                    System.out.println(v);
                }
            }
            return true;
        }

        // in the case of an SAT collision between a wall and circle
        if (s1 instanceof WallEntity && s2 instanceof CircleEntity) {
            WallEntity w = (WallEntity) s1;
            CircleEntity c = (CircleEntity) s2;

            if (w.start.x == 33 && w.end.x == 33) { // if statements check position of circle while accounting for radius against the bus boundaries
                if (c.position.x - c.radius < 33) {
                    return true;
                }
            } else if (w.start.x == 569 && w.end.x == 569) {
                if (c.position.x + c.radius > 569) {
                    return true;
                }
            } else if (w.start.y == 95 && w.end.y == 95) {
                if (c.position.y - c.radius < 95) {
                    return true;
                }
            } else {
                if (c.position.y + c.radius > 410) {
                    return true;
                }
            }
            return false;
        }

        // in the case of a collision between two circles
        if (s1 instanceof CircleEntity && s2 instanceof CircleEntity) {
            CircleEntity c1 = (CircleEntity) s1;
            CircleEntity c2 = (CircleEntity) s2;

            Vector2D center1 = new Vector2D(c1.position);
            Vector2D center2 = new Vector2D(c2.position);

            Vector2D n = center2.sub(center1);
            double distance = n.magnitude();

            if (distance < c1.radius + c2.radius) { // compares the distance between both circles' centers and the sum of their radii
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    public static ArrayList<Vector2D> filterIntersection(ArrayList<Vector2D> r1, ArrayList<Vector2D> r2) {
        ArrayList<Vector2D> result = new ArrayList<>();

        List<String> as = r1.stream().map(Vector2D::toString).collect(Collectors.toList()); // lambda expression maps all vectors to Strings
        List<String> bs = r2.stream().map(Vector2D::toString).collect(Collectors.toList());


        for (int i = 0; i < as.size(); i++) {
            if (bs.indexOf(as.get(i)) != -1) {
                result.add(r1.get(i));
            }
        }
        return result;
    }

    // isCollidingAABB method to detect collisions of Axis-Aligned-Bounding-Boxes (boxes on same axis)
    public static boolean isCollidingAABB(SimulationEntity s1, SimulationEntity s2) {
        AABB = false;
        if (s1.rotPosition == s2.rotPosition || s1.rotPosition == (s2.rotPosition + 0.25 * Math.PI) || s1.rotPosition == (s2.rotPosition + 0.5 * Math.PI) || s1.rotPosition == (s2.rotPosition + 0.75 * Math.PI)) {

            // in the case of a collision between a wall segment and a rectangle
            if (s1 instanceof WallEntity && s2 instanceof RectangleEntity) {
                WallEntity w = (WallEntity) s1;
                RectangleEntity r = (RectangleEntity) s2;

                double minX = w.start.x;    // sets up the minimum and maximum points of the wall
                double maxX = w.end.x;

                if (w.start.x > w.end.x) {
                    minX = w.end.x;
                    maxX = w.start.x;
                }

                List<Double> rVerticesX = r.vertices().stream().map(p -> p.x).collect(Collectors.toList()); // maps all the points' x components to a new list
                List<Double> rVerticesY = r.vertices().stream().map(p -> p.y).collect(Collectors.toList()); // maps all the points' y components to a new list

                if (maxX > Collections.max(rVerticesX)) {   // checks max of wall against the max value in the list of x vertices
                    maxX = Collections.max(rVerticesX);
                }

                if (minX < Collections.min(rVerticesX)) {   // checks min of wall against the min value in the list of x vertices
                    minX = Collections.min(rVerticesX);
                }

                if (minX > maxX) {  // if false (no collision), calls the SAT algorithm to check for a non-AABB collision
                    return isCollidingSAT(s1, s2);
                }

                double minY = w.start.y;
                double maxY = w.end.y;

                double dx = w.end.x - w.start.x;

                if (Math.abs(dx) > 0.0000001) {
                    double a = (w.end.y - w.start.y) / dx;
                    double b = w.start.y - a * w.start.x;
                    minY = a * minX + b;
                    maxY = a * maxX + b;
                }

                if (minY > maxY) {
                    double temp = maxY;
                    maxY = minY;
                    minY = temp;
                }

                if (maxY > Collections.max(rVerticesY)) {  // checks max of wall against the max value in the list of y vertices
                    maxY = Collections.max(rVerticesY);
                }

                if (minY < Collections.min(rVerticesY)) {  // checks min of wall against the min value in the list of y vertices
                    minY = Collections.min(rVerticesY);
                }

                if (minY > maxY) {  // if false (no collision), calls the SAT algorithm to check for a non-AABB collision
                    return isCollidingSAT(s1, s2);
                }
                AABB = true;    // sets AABB to true so that collision response is accordingly
                return true;    // returns isCollidedSAT() as true
            }

            // in the case of a collision between two rectangles
            if (s1 instanceof RectangleEntity && s2 instanceof RectangleEntity) {
                RectangleEntity r1 = (RectangleEntity) s1;
                RectangleEntity r2 = (RectangleEntity) s2;
                if (r1.topLeft.x < (r2.topLeft.x + r2.width) && (r1.topLeft.x + r1.width) > r2.topLeft.x && r1.topLeft.y < (r2.topLeft.y + r2.height) && (r1.height + r1.topLeft.y) > r2.topLeft.y) {   // compares rectangle vertices against other rectangle vertices
                    AABB = true;    // sets AABB to true so that collision response is accordingly
                    return true;    // returns isCollidedSAT() as true
                }
            } else {
                return isCollidingSAT(s1, s2); // if false (no collision), calls the SAT algorithm to check for a non-AABB collision
            }
            return false;
        }
        return false;
    }
}