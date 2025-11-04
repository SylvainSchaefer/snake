package model.player;

import model.Snake;
import util.Helper;
import model.Direction;
import java.awt.Point;
import java.io.Serializable;
import java.util.*;

/**
 * Classe abstraite pour les joueurs IA
 */
public abstract class AIPlayer implements Player, Serializable {
    private static final long serialVersionUID = 1L;
    protected Random random = new Random();
    protected int successRate; // Pourcentage de chance de faire le bon mouvement

    public AIPlayer(int successRate) {
        this.successRate = successRate;
    }

    @Override
    public void updateDirection(Snake snake, Point apple, int boardWidth, int boardHeight) {
        if (random.nextInt(100) < successRate) {
            Direction bestDir = calculateBestDirection(snake, apple, boardWidth, boardHeight);
            if (bestDir != null && snake.canChangeDirection(bestDir)) {
                snake.setDirection(bestDir);
            }
        }
    }

    protected Direction calculateBestDirection(Snake snake, Point apple, int boardWidth, int boardHeight) {
        Point head = snake.getHead();

        // Calculer la direction optimale vers la pomme
        List<Direction> priorityDirs = new ArrayList<>();

        int dx = apple.x - head.x;
        int dy = apple.y - head.y;

        // Prioriser les directions selon la distance à la pomme
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0)
                priorityDirs.add(Direction.RIGHT);
            else
                priorityDirs.add(Direction.LEFT);
            if (dy > 0)
                priorityDirs.add(Direction.DOWN);
            else if (dy < 0)
                priorityDirs.add(Direction.UP);
        } else {
            if (dy > 0)
                priorityDirs.add(Direction.DOWN);
            else
                priorityDirs.add(Direction.UP);
            if (dx > 0)
                priorityDirs.add(Direction.RIGHT);
            else if (dx < 0)
                priorityDirs.add(Direction.LEFT);
        }

        // Ajouter les autres directions
        for (Direction dir : Direction.values()) {
            if (!priorityDirs.contains(dir)) {
                priorityDirs.add(dir);
            }
        }

        // Trouver la première direction valide
        for (Direction dir : priorityDirs) {
            if (snake.canChangeDirection(dir) && isSafeDirection(snake, dir, boardWidth, boardHeight)) {
                return dir;
            }
        }

        // Si aucune direction sûre, essayer de survivre
        for (Direction dir : Direction.values()) {
            if (snake.canChangeDirection(dir) && !willHitWall(snake, dir, boardWidth, boardHeight)) {
                return dir;
            }
        }

        return null;
    }

    protected boolean isSafeDirection(Snake snake, Direction dir, int boardWidth, int boardHeight) {
        int unitSize = Helper.getUnitSize(boardWidth, boardHeight);
        Point nextPos = snake.getNextPosition(dir, unitSize);
        return !snake.wouldCollideWithSelf(nextPos) &&
                !willHitWall(snake, dir, boardWidth, boardHeight);
    }

    protected boolean willHitWall(Snake snake, Direction dir, int boardWidth, int boardHeight) {
        int unitSize = Helper.getUnitSize(boardWidth, boardHeight);
        Point nextPos = snake.getNextPosition(dir, unitSize);
        return nextPos.x < 0 || nextPos.x >= boardWidth ||
                nextPos.y < 0 || nextPos.y >= boardHeight;
    }
}

/**
 * IA Facile - 30% de réussite
 */
class EasyAI extends AIPlayer {
    private static final long serialVersionUID = 1L;

    public EasyAI() {
        super(30);
    }

    @Override
    public String getName() {
        return "IA Facile";
    }

    @Override
    public PlayerType getType() {
        return PlayerType.AI_EASY;
    }
}

/**
 * IA Moyenne - 60% de réussite
 */
class MediumAI extends AIPlayer {
    private static final long serialVersionUID = 1L;

    public MediumAI() {
        super(60);
    }

    @Override
    public String getName() {
        return "IA Moyenne";
    }

    @Override
    public PlayerType getType() {
        return PlayerType.AI_MEDIUM;
    }
}

/**
 * IA Difficile - 90% de réussite avec stratégie avancée
 */
class HardAI extends AIPlayer {
    private static final long serialVersionUID = 1L;

    public HardAI() {
        super(90);
    }

    @Override
    protected Direction calculateBestDirection(Snake snake, Point apple, int boardWidth, int boardHeight) {
        // Version améliorée avec pathfinding basique
        Direction bestDir = findPathToApple(snake, apple, boardWidth, boardHeight);
        if (bestDir != null) {
            return bestDir;
        }
        return super.calculateBestDirection(snake, apple, boardWidth, boardHeight);
    }

    private Direction findPathToApple(Snake snake, Point apple, int boardWidth, int boardHeight) {
        // Simple BFS pour trouver le chemin le plus court
        Queue<PathNode> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        int unitSize = Helper.getUnitSize(boardWidth, boardHeight);

        for (Direction dir : Direction.values()) {
            if (snake.canChangeDirection(dir) && isSafeDirection(snake, dir, boardWidth, boardHeight)) {
                Point nextPos = snake.getNextPosition(dir, unitSize);
                queue.offer(new PathNode(nextPos, dir));
                visited.add(nextPos);
            }
        }

        while (!queue.isEmpty()) {
            PathNode node = queue.poll();

            if (node.position.equals(apple)) {
                return node.firstDirection;
            }

            // Explorer les voisins (limité en profondeur pour performance)
            if (node.depth < 5) {
                for (Direction dir : Direction.values()) {
                    Point nextPos = getNextPosition(node.position, dir, unitSize);
                    if (!visited.contains(nextPos) &&
                            nextPos.x >= 0 && nextPos.x < boardWidth &&
                            nextPos.y >= 0 && nextPos.y < boardHeight &&
                            !snake.occupies(nextPos.x, nextPos.y)) {
                        queue.offer(new PathNode(nextPos, node.firstDirection, node.depth + 1));
                        visited.add(nextPos);
                    }
                }
            }
        }

        return null;
    }

    private Point getNextPosition(Point current, Direction dir, int unitSize) {
        Point next = new Point(current.x, current.y);
        switch (dir) {
            case UP:
                next.y -= unitSize;
                break;
            case DOWN:
                next.y += unitSize;
                break;
            case LEFT:
                next.x -= unitSize;
                break;
            case RIGHT:
                next.x += unitSize;
                break;
        }
        return next;
    }

    private class PathNode {
        Point position;
        Direction firstDirection;
        int depth;

        PathNode(Point position, Direction firstDirection, int depth) {
            this.position = position;
            this.firstDirection = firstDirection;
            this.depth = depth;
        }

        PathNode(Point position, Direction firstDirection) {
            this(position, firstDirection, 0);
        }
    }

    @Override
    public String getName() {
        return "IA Difficile";
    }

    @Override
    public PlayerType getType() {
        return PlayerType.AI_HARD;
    }
}