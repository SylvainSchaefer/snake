package model;

import model.player.Player;
import java.awt.Point;
import java.io.*;

/**
 * Classe pour gérer la sauvegarde et le chargement des parties
 */
public class SaveState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Snake snake1;
    private Snake snake2;
    private Player player1;
    private Player player2;
    private Point apple;
    private int player1Score;
    private int player2Score;
    
    public SaveState(GameModel model) {
        this.snake1 = model.getSnake1();
        this.snake2 = model.getSnake2();
        this.player1 = model.getPlayer1();
        this.player2 = model.getPlayer2();
        this.apple = model.getApple();
        this.player1Score = model.getPlayer1Score();
        this.player2Score = model.getPlayer2Score();
    }
    
    public void save(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        }
    }
    
    public static SaveState load(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (SaveState) in.readObject();
        }
    }
    
    public void restoreToModel(GameModel model) {
        model.setGameState(snake1, snake2, player1, player2, apple, player1Score, player2Score);
    }
    
    // Getters
    public Snake getSnake1() { return snake1; }
    public Snake getSnake2() { return snake2; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public Point getApple() { return apple; }
    public int getPlayer1Score() { return player1Score; }
    public int getPlayer2Score() { return player2Score; }
}