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
    private static final int INITIAL_SIZE = 3;

    private List<Point> body;
    private Direction direction;
    private Color color;
    private boolean growing;

    public Snake(int x, int y, Color color, Direction initialDirection, int unitSize) {
        this.color = color;
        this.direction = initialDirection;
        this.body = new ArrayList<>();
        this.growing = false;

        // Initialiser le corps du serpent
        for (int i = 0; i < INITIAL_SIZE; i++) {
            body.add(new Point(x - i * unitSize * initialDirection.getDx(),
                    y - i * unitSize * initialDirection.getDy()));
        }
    }

    public void move(int unitSize) {
        Point newHead = getNextPosition(direction, unitSize);
        body.add(0, newHead);

        if (!growing) {
            body.remove(body.size() - 1);
        } else {
            growing = false;
        }
    }

    public Point getNextPosition(Direction dir, int unitSize) {
        Point head = getHead();
        return new Point(head.x + dir.getDx() * unitSize,
                head.y + dir.getDy() * unitSize);
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

    public boolean checkWallCollision(int width, int height, int unitSize) {
        Point head = getHead();
        return head.x < 0 || head.x >= width ||
                head.y < 2 * unitSize || head.y >= height - 2 * unitSize;
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

    public void respawn(int x, int y, Direction newDirection, int unitSize) {
        body.clear();
        this.direction = newDirection;

        for (int i = 0; i < INITIAL_SIZE; i++) {
            body.add(new Point(x - i * unitSize * newDirection.getDx(),
                    y - i * unitSize * newDirection.getDy()));
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