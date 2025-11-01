package model.player;

import model.Snake;
import java.awt.Point;

/**
 * Interface Player pour le polymorphisme entre joueur humain et IA
 */
public interface Player {
    void updateDirection(Snake snake, Point apple, int boardWidth, int boardHeight);

    String getName();

    PlayerType getType();

    enum PlayerType {
        HUMAN, AI_EASY, AI_MEDIUM, AI_HARD
    }
}