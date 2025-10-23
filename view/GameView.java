package view;

import observer.GameObserver;
import model.GameModel;
import model.Snake;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Vue principale du jeu
 */
public class GameView extends JPanel implements GameObserver {
    private static final int BOARD_WIDTH = 800;
    private static final int BOARD_HEIGHT = 800;
    private static final int UNIT_SIZE = 20;

    private static final int MESSAGE_DURATION = 2000; // 2 secondes
    private static final int MESSAGE_SPACING = 25; // Espacement entre les messages

    private GameModel model;
    private ArrayList<StatusMessage> statusMessages = new ArrayList<>();
    private boolean showPauseMenu = false;

    // Classe interne pour gérer les messages avec leur timestamp
    private static class StatusMessage {
        String text;
        long creationTime;
        float alpha; // Transparence pour l'effet de fondu

        StatusMessage(String text) {
            this.text = text;
            this.creationTime = System.currentTimeMillis();
            this.alpha = 1.0f;
        }

        // Retourne la transparence actuelle basée sur le temps écoulé
        float getAlpha() {
            long elapsed = System.currentTimeMillis() - creationTime;
            if (elapsed > MESSAGE_DURATION) {
                return 0f;
            }
            // Fondu progressif dans les 500 dernières millisecondes
            if (elapsed > MESSAGE_DURATION - 500) {
                return (MESSAGE_DURATION - elapsed) / 500f;
            }
            return 1.0f;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - creationTime > MESSAGE_DURATION;
        }
    }

    public GameView(GameModel model) {
        this.model = model;
        model.addObserver(this);

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        // Timer pour nettoyer les messages expirés et rafraîchir l'affichage
        Timer cleanupTimer = new Timer(50, e -> {
            removeExpiredMessages();
            if (!statusMessages.isEmpty()) {
                repaint();
            }
        });
        cleanupTimer.start();
    }

    // Ajoute un nouveau message à la liste
    private void addStatusMessage(String message) {
        statusMessages.add(new StatusMessage(message));
        repaint();
    }

    // Supprime les messages expirés
    private void removeExpiredMessages() {
        Iterator<StatusMessage> iterator = statusMessages.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isExpired()) {
                iterator.remove();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (model == null || !model.isRunning()) {
            drawGameOver(g);
            return;
        }

        // Dessiner la grille (optionnel)
        drawGrid(g);

        // Dessiner la pomme
        if (model.getApple() != null) {
            drawApple(g, model.getApple());
        }

        // Dessiner les serpents
        if (model.getSnake1() != null) {
            drawSnake(g, model.getSnake1(), 1);
        }
        if (model.getSnake2() != null) {
            drawSnake(g, model.getSnake2(), 2);
        }

        // Afficher les scores et infos
        drawScores(g);

        // Afficher le menu pause si nécessaire
        if (showPauseMenu) {
            drawPauseMenu(g);
        }

        // Afficher les messages de statut empilés
        if (!statusMessages.isEmpty()) {
            drawStatusMessages(g);
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
        // Dessiner une pomme plus jolie
        g.setColor(Color.RED);
        g.fillOval(apple.x + 2, apple.y + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);

        // Petite feuille
        g.setColor(Color.GREEN);
        g.fillRect(apple.x + UNIT_SIZE / 2 - 1, apple.y, 2, 4);
    }

    private void drawSnake(Graphics g, Snake snake, int playerNumber) {
        java.util.List<Point> body = snake.getBody();
        Color baseColor = snake.getColor();

        for (int i = 0; i < body.size(); i++) {
            Point segment = body.get(i);

            if (i == 0) {
                // Tête du serpent - plus lumineuse
                g.setColor(baseColor.brighter());
                g.fillRoundRect(segment.x, segment.y, UNIT_SIZE, UNIT_SIZE, 5, 5);

                // Dessiner les yeux
                g.setColor(Color.WHITE);
                int eyeSize = 4;
                int eyeOffset = 4;

                switch (snake.getDirection()) {
                    case UP:
                        g.fillOval(segment.x + eyeOffset, segment.y + eyeOffset, eyeSize, eyeSize);
                        g.fillOval(segment.x + UNIT_SIZE - eyeOffset - eyeSize, segment.y + eyeOffset, eyeSize,
                                eyeSize);
                        break;
                    case DOWN:
                        g.fillOval(segment.x + eyeOffset, segment.y + UNIT_SIZE - eyeOffset - eyeSize, eyeSize,
                                eyeSize);
                        g.fillOval(segment.x + UNIT_SIZE - eyeOffset - eyeSize,
                                segment.y + UNIT_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                        break;
                    case LEFT:
                        g.fillOval(segment.x + eyeOffset, segment.y + eyeOffset, eyeSize, eyeSize);
                        g.fillOval(segment.x + eyeOffset, segment.y + UNIT_SIZE - eyeOffset - eyeSize, eyeSize,
                                eyeSize);
                        break;
                    case RIGHT:
                        g.fillOval(segment.x + UNIT_SIZE - eyeOffset - eyeSize, segment.y + eyeOffset, eyeSize,
                                eyeSize);
                        g.fillOval(segment.x + UNIT_SIZE - eyeOffset - eyeSize,
                                segment.y + UNIT_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                        break;
                }
            } else {
                // Corps du serpent - gradient
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
        // Zone de score avec fond semi-transparent
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, BOARD_WIDTH, 40);

        // Scores
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        String player1Name = model.getPlayer1() != null ? model.getPlayer1().getName() : "Joueur 1";
        String player2Name = model.getPlayer2() != null ? model.getPlayer2().getName() : "Joueur 2";

        g.drawString(player1Name + ": " + model.getPlayer1Score(), 10, 25);
        g.drawString(player2Name + ": " + model.getPlayer2Score(), BOARD_WIDTH - 150, 25);

        // Longueur des serpents
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
        // Fond semi-transparent
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        // Texte PAUSE
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String pauseText = "PAUSE";
        FontMetrics fm = getFontMetrics(g.getFont());
        int x = (BOARD_WIDTH - fm.stringWidth(pauseText)) / 2;
        g.drawString(pauseText, x, BOARD_HEIGHT / 2 - 50);

        // Instructions
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
        // Fond
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        // Titre Game Over
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOver = "GAME OVER";
        FontMetrics fm = getFontMetrics(g.getFont());
        int x = (BOARD_WIDTH - fm.stringWidth(gameOver)) / 2;
        g.drawString(gameOver, x, BOARD_HEIGHT / 2 - 100);

        // Gagnant
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

        // Scores finaux
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        String finalScore = "Score final: " + model.getPlayer1Score() + " - " + model.getPlayer2Score();
        fm = getFontMetrics(g.getFont());
        x = (BOARD_WIDTH - fm.stringWidth(finalScore)) / 2;
        g.drawString(finalScore, x, BOARD_HEIGHT / 2 + 50);

        // Instruction
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String instruction = "Appuyez sur ESPACE pour retourner au menu";
        fm = getFontMetrics(g.getFont());
        x = (BOARD_WIDTH - fm.stringWidth(instruction)) / 2;
        g.drawString(instruction, x, BOARD_HEIGHT / 2 + 100);
    }

    private void drawStatusMessages(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();

        // Dessiner les messages du plus ancien au plus récent (de bas en haut)
        int yPosition = BOARD_HEIGHT - 80;

        for (int i = statusMessages.size() - 1; i >= 0; i--) {
            StatusMessage msg = statusMessages.get(i);
            float alpha = msg.getAlpha();

            if (alpha > 0) {
                // Appliquer la transparence
                g2d.setColor(new Color(255, 255, 0, (int) (255 * alpha))); // Jaune avec alpha

                int x = (BOARD_WIDTH - fm.stringWidth(msg.text)) / 2;
                g2d.drawString(msg.text, x, yPosition);

                yPosition -= MESSAGE_SPACING; // Monter pour le prochain message
            }
        }
    }

    // Implémentation de GameObserver
    @Override
    public void onScoreUpdate(int player1Score, int player2Score) {
        // repaint();
    }

    @Override
    public void onGameStateChange(GameState state) {
        showPauseMenu = (state == GameState.PAUSED);
        // repaint();
    }

    @Override
    public void onSnakeMove() {
        // repaint();
    }

    @Override
    public void onAppleEaten(String playerName) {
        addStatusMessage(playerName + " a mangé la pomme!");
    }

    @Override
    public void onCollision(String playerName) {
        addStatusMessage(playerName + " a eu une collision!");
    }
}