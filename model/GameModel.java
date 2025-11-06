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
 * Ajout : Pomme dorée (bonus)
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
    private Point goldenApple; // 🍏 Pomme dorée

    private int player1Score;
    private int player2Score;
    private boolean running;
    private boolean paused;
    private Random random;

    // Timers pour la pomme dorée
    private long lastGoldenAppleSpawn;
    private long goldenAppleSpawnTime;
    private long nextGoldenAppleDelay;

    private static final int GOLDEN_APPLE_MIN_INTERVAL = 20000; // 20s
    private static final int GOLDEN_APPLE_MAX_INTERVAL = 30000; // 30s
    private static final int GOLDEN_APPLE_DURATION = 7000; // visible pendant 7s
    private static final int GOLDEN_APPLE_POINTS = 5;

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

        // Config pomme dorée
        goldenApple = null;
        lastGoldenAppleSpawn = System.currentTimeMillis();
        nextGoldenAppleDelay = getRandomGoldenAppleDelay();

        running = true;
        paused = false;

        notifyGameStateChange(GameObserver.GameState.PLAYING);
    }

    private long getRandomGoldenAppleDelay() {
        return GOLDEN_APPLE_MIN_INTERVAL + random.nextInt(GOLDEN_APPLE_MAX_INTERVAL - GOLDEN_APPLE_MIN_INTERVAL);
    }

    public void update() {
        if (!running || paused)
            return;

        player1.updateDirection(snake1, apple, BOARD_WIDTH, BOARD_HEIGHT);
        player2.updateDirection(snake2, apple, BOARD_WIDTH, BOARD_HEIGHT);

        snake1.move();
        snake2.move();
        notifySnakeMove();

        // Gestion des pommes
        checkAppleCollisions();
        handleGoldenApple();
        checkGoldenAppleCollisions();

        // Collisions classiques
        checkCollisions();
    }

    private void handleGoldenApple() {
        long now = System.currentTimeMillis();

        // Apparition
        if (goldenApple == null && now - lastGoldenAppleSpawn > nextGoldenAppleDelay) {
            generateGoldenApple();
            goldenAppleSpawnTime = now;
            lastGoldenAppleSpawn = now;
            notifyGoldenAppleSpawned();
        }

        // Disparition
        if (goldenApple != null && now - goldenAppleSpawnTime > GOLDEN_APPLE_DURATION) {
            goldenApple = null;
            notifyGoldenAppleDisappeared();
            lastGoldenAppleSpawn = now;
            nextGoldenAppleDelay = getRandomGoldenAppleDelay();
        }
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

    private void checkGoldenAppleCollisions() {
        if (goldenApple == null)
            return;

        boolean eaten = false;

        if (snake1.getHead().equals(goldenApple)) {
            snake1.grow();
            player1Score += GOLDEN_APPLE_POINTS;
            notifyGoldenAppleEaten(player1.getName());
            notifyScoreUpdate(player1Score, player2Score);
            eaten = true;
        } else if (snake2.getHead().equals(goldenApple)) {
            snake2.grow();
            player2Score += GOLDEN_APPLE_POINTS;
            notifyGoldenAppleEaten(player2.getName());
            notifyScoreUpdate(player1Score, player2Score);
            eaten = true;
        }

        if (eaten) {
            goldenApple = null;
            lastGoldenAppleSpawn = System.currentTimeMillis();
            nextGoldenAppleDelay = getRandomGoldenAppleDelay();
        }
    }

    private void checkCollisions() {
        if (snake1.checkSelfCollision() || snake1.checkWallCollision(BOARD_WIDTH, BOARD_HEIGHT)) {
            player2Score += 5;
            notifyCollision(player1.getName());
            notifyScoreUpdate(player1Score, player2Score);
            respawnSnake(snake1);
        }

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
            y = random.nextInt((BOARD_HEIGHT - 80) / UNIT_SIZE) * UNIT_SIZE + 40;
        } while (isPositionOccupied(x, y));

        apple = new Point(x, y);
    }

    private void generateGoldenApple() {
        int x, y;
        do {
            x = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            y = random.nextInt((BOARD_HEIGHT - 80) / UNIT_SIZE) * UNIT_SIZE + 40;
        } while (isPositionOccupied(x, y) || (apple != null && apple.equals(new Point(x, y))));

        goldenApple = new Point(x, y);
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
    public Snake getSnake1() { return snake1; }
    public Snake getSnake2() { return snake2; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public Point getApple() { return apple; }
    public Point getGoldenApple() { return goldenApple; }
    public int getPlayer1Score() { return player1Score; }
    public int getPlayer2Score() { return player2Score; }
    public boolean isRunning() { return running; }
    public boolean isPaused() { return paused; }

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
        this.goldenApple = null;
        this.lastGoldenAppleSpawn = System.currentTimeMillis();
        this.nextGoldenAppleDelay = getRandomGoldenAppleDelay();
    }
}
