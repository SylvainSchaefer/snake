package model.player;

import model.Snake;
import model.Direction;
import java.awt.Point;
import java.io.Serializable;

/**
 * Implémentation d'un joueur humain
 */
public class HumanPlayer implements Player, Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Direction pendingDirection;

    public HumanPlayer(String name) {
        this.name = name;
        this.pendingDirection = null;
    }

    @Override
    public void updateDirection(Snake snake, Point apple, int boardWidth, int boardHeight) {
        // Pour un joueur humain, on applique simplement la direction en attente
        if (pendingDirection != null && snake.canChangeDirection(pendingDirection)) {
            snake.setDirection(pendingDirection);
            pendingDirection = null;
        }
    }

    public void setPendingDirection(Direction direction) {
        this.pendingDirection = direction;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PlayerType getType() {
        return PlayerType.HUMAN;
    }
}