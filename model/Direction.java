package model;

/**
 * Enum représentant les directions possibles
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);
    
    private final int dx;
    private final int dy;
    
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    public int getDx() {
        return dx;
    }
    
    public int getDy() {
        return dy;
    }
    
    public boolean isOpposite(Direction other) {
        return (this.dx == -other.dx && this.dy == -other.dy);
    }
    
    public static Direction fromString(String str) {
        try {
            return Direction.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}