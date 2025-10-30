package model;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un serpent
 */
public class Snake implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int UNIT_SIZE = 20;
    private static final int INITIAL_SIZE = 3;

    private List<Point> body;
    private Direction direction;
    private Color color;
    private boolean growing;

    public Snake(int x, int y, Color color, Direction initialDirection) {
        this.color = color;
        this.direction = initialDirection;
        this.body = new ArrayList<>();
        this.growing = false;

        // Initialiser le corps du serpent
        for (int i = 0; i < INITIAL_SIZE; i++) {
            body.add(new Point(x - i * UNIT_SIZE * initialDirection.getDx(),
                    y - i * UNIT_SIZE * initialDirection.getDy()));
        }
    }

    public void move() {
        Point newHead = getNextPosition(direction);
        body.add(0, newHead);

        if (!growing) {
            body.remove(body.size() - 1);
        } else {
            growing = false;
        }
    }

    public Point getNextPosition(Direction dir) {
        Point head = getHead();
        return new Point(head.x + dir.getDx() * UNIT_SIZE,
                head.y + dir.getDy() * UNIT_SIZE);
    }

    public void grow() {
        growing = true;
    }

    public boolean checkSelfCollision() {
        Point head = getHead();
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean checkWallCollision(int width, int height) {
        Point head = getHead();
        return head.x < 0 || head.x >= width ||
                head.y < 2 * UNIT_SIZE || head.y >= height - 2 * UNIT_SIZE;
    }

    public boolean occupies(int x, int y) {
        for (Point segment : body) {
            if (segment.x == x && segment.y == y) {
                return true;
            }
        }
        return false;
    }

    public boolean wouldCollideWithSelf(Point position) {
        for (int i = 1; i < body.size(); i++) {
            if (position.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void respawn(int x, int y, Direction newDirection) {
        body.clear();
        this.direction = newDirection;

        for (int i = 0; i < INITIAL_SIZE; i++) {
            body.add(new Point(x - i * UNIT_SIZE * newDirection.getDx(),
                    y - i * UNIT_SIZE * newDirection.getDy()));
        }
    }

    public boolean canChangeDirection(Direction newDirection) {
        return newDirection != null && !direction.isOpposite(newDirection);
    }

    // Getters et Setters
    public Point getHead() {
        return new Point(body.get(0));
    }

    public List<Point> getBody() {
        return new ArrayList<>(body);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        if (canChangeDirection(direction)) {
            this.direction = direction;
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getLength() {
        return body.size();
    }
}