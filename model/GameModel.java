package model;

import observer.Observable;
import observer.GameObserver;
import model.player.Player;
import model.player.HumanPlayer;
import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.Random;

/**
 * Modèle principal du jeu - gère la logique
 */
public class GameModel extends Observable implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int BOARD_WIDTH = 800;
    public static final int BOARD_HEIGHT = 800;
    public static final int UNIT_SIZE = 20;

    private Snake snake1;
    private Snake snake2;
    private Player player1;
    private Player player2;
    private Point apple;
    private int player1Score;
    private int player2Score;
    private boolean running;
    private boolean paused;
    private Random random;

    public GameModel() {
        this.random = new Random();
        this.running = false;
        this.paused = false;
    }

    public void initGame(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;

        // Initialiser les serpents
        snake1 = new Snake(100, 100, Color.GREEN, Direction.RIGHT);
        snake2 = new Snake(400, 400, Color.BLUE, Direction.LEFT);

        // Initialiser les scores
        player1Score = 0;
        player2Score = 0;

        // Placer la première pomme
        generateNewApple();

        running = true;
        paused = false;

        notifyGameStateChange(GameObserver.GameState.PLAYING);
    }

    public void update() {
        if (!running || paused)
            return;

        // Enlever les commentaires pour tester les perfs
        // long start = System.nanoTime();

        // long t1 = System.nanoTime();
        player1.updateDirection(snake1, apple, BOARD_WIDTH, BOARD_HEIGHT);
        player2.updateDirection(snake2, apple, BOARD_WIDTH, BOARD_HEIGHT);
        // long t2 = System.nanoTime();

        snake1.move();
        snake2.move();
        // long t3 = System.nanoTime();

        notifySnakeMove();
        // long t4 = System.nanoTime();

        checkAppleCollisions();
        // long t5 = System.nanoTime();

        checkCollisions();
        // long t6 = System.nanoTime();

        /*
         * long total = (t6 - start) / 1_000; // ms
         * if (total > 70) { // Seulement si ça prend longtemps
         * System.out.printf(
         * "update total=%dms | dir=%dms | move=%dms | notify=%dms | apple=%dms | coll=%dms%n"
         * ,
         * total,
         * (t2 - t1) / 1_000,
         * (t3 - t2) / 1_000,
         * (t4 - t3) / 1_000,
         * (t5 - t4) / 1_000,
         * (t6 - t5) / 1_000
         * );
         * }
         */
    }

    private void checkAppleCollisions() {
        boolean newAppleNeeded = false;

        if (snake1.getHead().equals(apple)) {
            snake1.grow();
            player1Score++;
            notifyAppleEaten(player1.getName());
            notifyScoreUpdate(player1Score, player2Score);
            newAppleNeeded = true;
        }

        if (snake2.getHead().equals(apple)) {
            snake2.grow();
            player2Score++;
            notifyAppleEaten(player2.getName());
            notifyScoreUpdate(player1Score, player2Score);
            newAppleNeeded = true;
        }

        if (newAppleNeeded) {
            generateNewApple();
        }
    }

    private void checkCollisions() {
        // Vérifier les collisions du serpent 1
        if (snake1.checkSelfCollision() || snake1.checkWallCollision(BOARD_WIDTH, BOARD_HEIGHT)) {
            player2Score += 5;
            notifyCollision(player1.getName());
            notifyScoreUpdate(player1Score, player2Score);
            respawnSnake(snake1);
        }

        // Vérifier les collisions du serpent 2
        if (snake2.checkSelfCollision() || snake2.checkWallCollision(BOARD_WIDTH, BOARD_HEIGHT)) {
            player1Score += 5;
            notifyCollision(player2.getName());
            notifyScoreUpdate(player1Score, player2Score);
            respawnSnake(snake2);
        }
    }

    private void respawnSnake(Snake snake) {
        int x, y;
        Direction[] directions = Direction.values();
        Direction newDirection;

        x = (random.nextInt(BOARD_WIDTH / UNIT_SIZE - 6) + 3) * UNIT_SIZE;
        y = (random.nextInt(BOARD_HEIGHT / UNIT_SIZE - 6) + 3) * UNIT_SIZE;
        newDirection = directions[random.nextInt(directions.length)];

        snake.respawn(x, y, newDirection);
    }

    private void generateNewApple() {
        int x, y;
        do {
            x = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            y = random.nextInt((BOARD_HEIGHT - 80) / UNIT_SIZE) * UNIT_SIZE + 40; // -60 puis +40 pour ne pas avoir de
                                                                                  // pomme au niveau du score, ni en
                                                                                  // dehors de la grille
        } while (isPositionOccupied(x, y));

        apple = new Point(x, y);
    }

    private boolean isPositionOccupied(int x, int y) {
        return (snake1 != null && snake1.occupies(x, y)) ||
                (snake2 != null && snake2.occupies(x, y));
    }

    public void togglePause() {
        paused = !paused;
        notifyGameStateChange(paused ? GameObserver.GameState.PAUSED : GameObserver.GameState.PLAYING);
    }

    public void endGame() {
        running = false;
        notifyGameStateChange(GameObserver.GameState.GAME_OVER);
    }

    // Méthodes pour les contrôles du joueur humain
    public void setPlayer1Direction(Direction direction) {
        if (player1 instanceof HumanPlayer) {
            ((HumanPlayer) player1).setPendingDirection(direction);
        }
    }

    public void setPlayer2Direction(Direction direction) {
        if (player2 instanceof HumanPlayer) {
            ((HumanPlayer) player2).setPendingDirection(direction);
        }
    }

    // Getters
    public Snake getSnake1() {
        return snake1;
    }

    public Snake getSnake2() {
        return snake2;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Point getApple() {
        return apple;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    // Setters pour le chargement
    public void setGameState(Snake snake1, Snake snake2, Player player1, Player player2,
            Point apple, int player1Score, int player2Score) {
        this.snake1 = snake1;
        this.snake2 = snake2;
        this.player1 = player1;
        this.player2 = player2;
        this.apple = apple;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.running = true;
        this.paused = false;
    }
}