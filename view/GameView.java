package view;

import observer.GameObserver;
import model.GameModel;
import model.Snake;
import javax.swing.*;
import java.awt.*;

/**
 * Vue principale du jeu (ajout de la pomme dorée)
 */
public class GameView extends JPanel implements GameObserver {
    private static final int BOARD_WIDTH = 800;
    private static final int BOARD_HEIGHT = 800;
    private static final int UNIT_SIZE = 20;

    private GameModel model;
    private String statusMessage = "";
    private boolean showPauseMenu = false;
    private int absoluteFrameCounter = 0;

    public GameView(GameModel model) {
        this.model = model;
        model.addObserver(this);

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        absoluteFrameCounter++;
        super.paintComponent(g);
        draw(g);

        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 20));
    }

    private void draw(Graphics g) {
        if (model == null || !model.isRunning()) {
            drawGameOver(g);
            return;
        }

        // Dessiner la grille
        drawGrid(g);

        // Pomme rouge classique
        if (model.getApple() != null) {
            drawApple(g, model.getApple());
        }

        // 🍏 Pomme dorée
        if (model.getGoldenApple() != null) {
            drawGoldenApple(g, model.getGoldenApple());
        }

        // Serpents
        if (model.getSnake1() != null) {
            drawSnake(g, model.getSnake1(), 1);
        }
        if (model.getSnake2() != null) {
            drawSnake(g, model.getSnake2(), 2);
        }

        // Scores
        drawScores(g);

        // Menu pause
        if (showPauseMenu) {
            drawPauseMenu(g);
        }

        // Message temporaire
        if (!statusMessage.isEmpty()) {
            drawStatusMessage(g);
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(new Color(40, 40, 40));
        for (int i = 0; i < BOARD_HEIGHT / UNIT_SIZE; i++) {
            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, BOARD_HEIGHT);
            g.drawLine(0, i * UNIT_SIZE, BOARD_WIDTH, i * UNIT_SIZE);
        }
    }

    private void drawApple(Graphics g, Point apple) {
        g.setColor(Color.RED);
        g.fillOval(apple.x + 2, apple.y + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);
        g.setColor(Color.GREEN);
        g.fillRect(apple.x + UNIT_SIZE / 2 - 1, apple.y, 2, 4);
    }

    // 🟡 Nouvelle méthode : pomme dorée
    private void drawGoldenApple(Graphics g, Point goldenApple) {
        // Corps de la pomme dorée
        g.setColor(new Color(255, 215, 0)); // Doré
        g.fillOval(goldenApple.x + 2, goldenApple.y + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);

        // Halo lumineux doux
        g.setColor(new Color(255, 255, 180, 120));
        g.fillOval(goldenApple.x - 3, goldenApple.y - 3, UNIT_SIZE + 6, UNIT_SIZE + 6);

        // Feuille verte
        g.setColor(new Color(0, 200, 0));
        g.fillRect(goldenApple.x + UNIT_SIZE / 2 - 1, goldenApple.y, 2, 4);
    }

    private void drawSnake(Graphics g, Snake snake, int playerNumber) {
        java.util.List<Point> body = snake.getBody();
        Color baseColor = snake.getColor();

        for (int i = 0; i < body.size(); i++) {
            Point segment = body.get(i);

            if (i == 0) {
                // Tête
                g.setColor(baseColor.brighter());
                g.fillRoundRect(segment.x, segment.y, UNIT_SIZE, UNIT_SIZE, 5, 5);

                // Yeux
                g.setColor(Color.WHITE);
                int eyeSize = 4;
                int eyeOffset = 4;

                switch (snake.getDirection()) {
                    case UP:
                        g.fillOval(segment.x + eyeOffset, segment.y + eyeOffset, eyeSize, eyeSize);
                        g.fillOval(segment.x + UNIT_SIZE - eyeOffset - eyeSize, segment.y + eyeOffset, eyeSize, eyeSize);
                        break;
                    case DOWN:
                        g.fillOval(segment.x + eyeOffset, segment.y + UNIT_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                        g.fillOval(segment.x + UNIT_SIZE - eyeOffset - eyeSize,
                                segment.y + UNIT_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                        break;
                    case LEFT:
                        g.fillOval(segment.x + eyeOffset, segment.y + eyeOffset, eyeSize, eyeSize);
                        g.fillOval(segment.x + eyeOffset, segment.y + UNIT_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                        break;
                    case RIGHT:
                        g.fillOval(segment.x + UNIT_SIZE - eyeOffset - eyeSize, segment.y + eyeOffset, eyeSize, eyeSize);
                        g.fillOval(segment.x + UNIT_SIZE - eyeOffset - eyeSize,
                                segment.y + UNIT_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                        break;
                }
            } else {
                // Corps du serpent avec léger dégradé
                float ratio = (float) i / body.size();
                Color segmentColor = new Color(
                        (int) (baseColor.getRed() * (1 - ratio * 0.3)),
                        (int) (baseColor.getGreen() * (1 - ratio * 0.3)),
                        (int) (baseColor.getBlue() * (1 - ratio * 0.3)));
                g.setColor(segmentColor);
                g.fillRect(segment.x + 1, segment.y + 1, UNIT_SIZE - 2, UNIT_SIZE - 2);
            }
        }
    }

    private void drawScores(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, BOARD_WIDTH, 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        String player1Name = model.getPlayer1() != null ? model.getPlayer1().getName() : "Joueur 1";
        String player2Name = model.getPlayer2() != null ? model.getPlayer2().getName() : "Joueur 2";

        g.drawString(player1Name + ": " + model.getPlayer1Score(), 10, 25);
        g.drawString(player2Name + ": " + model.getPlayer2Score(), BOARD_WIDTH - 150, 25);

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.LIGHT_GRAY);
        if (model.getSnake1() != null) {
            g.drawString("Taille: " + model.getSnake1().getLength(), 10, 38);
        }
        if (model.getSnake2() != null) {
            g.drawString("Taille: " + model.getSnake2().getLength(), BOARD_WIDTH - 150, 38);
        }
    }

    private void drawPauseMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String pauseText = "PAUSE";
        FontMetrics fm = getFontMetrics(g.getFont());
        int x = (BOARD_WIDTH - fm.stringWidth(pauseText)) / 2;
        g.drawString(pauseText, x, BOARD_HEIGHT / 2 - 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        String[] instructions = {
                "P - Reprendre",
                "S - Sauvegarder",
                "ESC - Retour au menu"
        };

        for (int i = 0; i < instructions.length; i++) {
            fm = getFontMetrics(g.getFont());
            x = (BOARD_WIDTH - fm.stringWidth(instructions[i])) / 2;
            g.drawString(instructions[i], x, BOARD_HEIGHT / 2 + 20 + (i * 30));
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOver = "GAME OVER";
        FontMetrics fm = getFontMetrics(g.getFont());
        int x = (BOARD_WIDTH - fm.stringWidth(gameOver)) / 2;
        g.drawString(gameOver, x, BOARD_HEIGHT / 2 - 100);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        String winner;
        if (model.getPlayer1Score() > model.getPlayer2Score()) {
            winner = model.getPlayer1().getName() + " gagne!";
        } else if (model.getPlayer2Score() > model.getPlayer1Score()) {
            winner = model.getPlayer2().getName() + " gagne!";
        } else {
            winner = "Égalité!";
        }
        fm = getFontMetrics(g.getFont());
        x = (BOARD_WIDTH - fm.stringWidth(winner)) / 2;
        g.drawString(winner, x, BOARD_HEIGHT / 2);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String finalScore = "Score final: " + model.getPlayer1Score() + " - " + model.getPlayer2Score();
        fm = getFontMetrics(g.getFont());
        x = (BOARD_WIDTH - fm.stringWidth(finalScore)) / 2;
        g.drawString(finalScore, x, BOARD_HEIGHT / 2 + 50);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String instruction = "Appuyez sur ESPACE pour retourner au menu";
        fm = getFontMetrics(g.getFont());
        x = (BOARD_WIDTH - fm.stringWidth(instruction)) / 2;
        g.drawString(instruction, x, BOARD_HEIGHT / 2 + 100);
    }

    private void drawStatusMessage(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = getFontMetrics(g.getFont());
        int x = (BOARD_WIDTH - fm.stringWidth(statusMessage)) / 2;
        g.drawString(statusMessage, x, BOARD_HEIGHT - 80);

        Timer timer = new Timer(2000, e -> statusMessage = "");
        timer.setRepeats(false);
        timer.start();
    }

    // === Observer ===
    @Override
    public void onScoreUpdate(int player1Score, int player2Score) {}

    @Override
    public void onGameStateChange(GameState state) {
        showPauseMenu = (state == GameState.PAUSED);
    }

    @Override
    public void onSnakeMove() {}

    @Override
    public void onAppleEaten(String playerName) {
        statusMessage = playerName + " a mangé la pomme!";
    }

    @Override
    public void onCollision(String playerName) {
        statusMessage = playerName + " a eu une collision!";
    }

    // Événements spécifiques à la pomme dorée
    @Override
    public void onGoldenAppleSpawned() {
        statusMessage = "⭐ Une pomme dorée est apparue ! ⭐";
    }

    @Override
    public void onGoldenAppleDisappeared() {
        statusMessage = "La pomme dorée a disparu...";
    }

    @Override
    public void onGoldenAppleEaten(String playerName) {
        statusMessage = "✨ " + playerName + " a mangé la pomme dorée ! (+5)";
    }

    @Override
    public void onBombSpawned() {

    }

    @Override
    public void onBombDisappeared() {

    }

    @Override
    public void onBombHit(String playerName) {

    }
}
