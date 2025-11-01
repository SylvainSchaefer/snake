package view;

import observer.GameObserver;
import model.Direction;
import model.GameModel;
import model.Snake;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Vue principale du jeu
 */
public class GameView extends JPanel implements GameObserver {
    private static final int BOARD_WIDTH = 800;
    private static final int BOARD_HEIGHT = 800;

    private static final int MESSAGE_DURATION = 2000; // 2 secondes
    private static final int MESSAGE_SPACING = 25; // Espacement entre les messages

    private GameModel model;
    private ArrayList<StatusMessage> statusMessages = new ArrayList<>();
    private boolean showPauseMenu = false;

    // Classe interne pour gérer les messages avec leur timestamp
    private static class StatusMessage {
        String text;
        long creationTime;

        StatusMessage(String text) {
            this.text = text;
            this.creationTime = System.currentTimeMillis();
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
        int bordWidth = this.getWidth();
        int bordHeight = this.getHeight();

        int minSize = bordWidth < bordHeight ? bordWidth : bordHeight;

        int unitSize = minSize / 40; // 40 cases dans la grille

        if (model == null || !model.isRunning()) {
            drawGameOver(g, bordWidth, bordHeight);
            return;
        }

        // Dessiner la grille (optionnel)
        drawGrid(g, unitSize, bordWidth, bordHeight);

        // Dessiner la pomme
        if (model.getApple() != null) {
            drawApple(g, model.getApple(), unitSize);
        }

        // Dessiner les serpents
        if (model.getSnake1() != null) {
            drawSnake(g, model.getSnake1(), 1, unitSize);
        }
        if (model.getSnake2() != null) {
            drawSnake(g, model.getSnake2(), 2, unitSize);
        }

        // Afficher les scores et infos
        drawScores(g, bordWidth);

        // Afficher le menu pause si nécessaire
        if (showPauseMenu) {
            drawPauseMenu(g, bordWidth, bordHeight);
        }

        // Afficher les messages de statut empilés
        if (!statusMessages.isEmpty()) {
            drawStatusMessages(g, bordWidth, bordHeight);
        }
    }

    private void drawGrid(Graphics g, int unitSize, int boardWidth, int bordHeight) {
        g.setColor(new Color(40, 40, 40));
        for (int i = 0; i < (bordHeight > boardWidth ? bordHeight : boardWidth) / unitSize; i++) {
            g.drawLine(i * unitSize, 0, i * unitSize, bordHeight);
            g.drawLine(0, i * unitSize, boardWidth, i * unitSize);
        }
    }

    private void drawApple(Graphics g, Point apple, int unitSize) {
        // Dessiner une pomme plus jolie
        g.setColor(Color.RED);
        g.fillOval(apple.x + 2, apple.y + 2, unitSize - 4, unitSize - 4);

        // Petite feuille
        g.setColor(Color.GREEN);
        g.fillRect(apple.x + unitSize / 2 - 1, apple.y, 2, 4);
    }

    private void drawSnake(Graphics g, Snake snake, int playerNumber, int unitSize) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        java.util.List<Point> body = snake.getBody();
        Color baseColor = snake.getColor();

        for (int i = 0; i < body.size(); i++) {
            Point segment = body.get(i);

            if (i == 0) {
                // Tête du serpent - arrondie selon la direction
                g2d.setColor(baseColor.brighter());
                drawRoundedRectangle(g2d, segment.x + 1, segment.y + 1, unitSize, unitSize,
                        10, snake.getDirection());

                // Dessiner les yeux
                g2d.setColor(Color.WHITE);
                int eyeSize = 4;
                int eyeOffset = 4;

                switch (snake.getDirection()) {
                    case UP:
                        g2d.fillOval(segment.x + eyeOffset, segment.y + eyeOffset, eyeSize, eyeSize);
                        g2d.fillOval(segment.x + unitSize - eyeOffset - eyeSize, segment.y + eyeOffset, eyeSize,
                                eyeSize);
                        break;
                    case DOWN:
                        g2d.fillOval(segment.x + eyeOffset, segment.y + unitSize - eyeOffset - eyeSize, eyeSize,
                                eyeSize);
                        g2d.fillOval(segment.x + unitSize - eyeOffset - eyeSize,
                                segment.y + unitSize - eyeOffset - eyeSize, eyeSize, eyeSize);
                        break;
                    case LEFT:
                        g2d.fillOval(segment.x + eyeOffset, segment.y + eyeOffset, eyeSize, eyeSize);
                        g2d.fillOval(segment.x + eyeOffset, segment.y + unitSize - eyeOffset - eyeSize, eyeSize,
                                eyeSize);
                        break;
                    case RIGHT:
                        g2d.fillOval(segment.x + unitSize - eyeOffset - eyeSize, segment.y + eyeOffset, eyeSize,
                                eyeSize);
                        g2d.fillOval(segment.x + unitSize - eyeOffset - eyeSize,
                                segment.y + unitSize - eyeOffset - eyeSize, eyeSize, eyeSize);
                        break;
                }
            } else if (i == body.size() - 1) {
                // Queue du serpent - arrondie du côté opposé à la direction
                float ratio = (float) i / body.size();
                Color segmentColor = new Color(
                        (int) (baseColor.getRed() * (1 - ratio * 0.3)),
                        (int) (baseColor.getGreen() * (1 - ratio * 0.3)),
                        (int) (baseColor.getBlue() * (1 - ratio * 0.3)));
                g2d.setColor(segmentColor);

                // Déterminer la direction de la queue (opposée au segment précédent)
                Point prevSegment = body.get(i - 1);
                Direction tailDirection = getTailDirection(segment, prevSegment);
                drawRoundedRectangle(g2d, segment.x + 1, segment.y + 1, unitSize, unitSize,
                        10, tailDirection);
            } else {
                // Corps du serpent - gradient
                float ratio = (float) i / body.size();
                Color segmentColor = new Color(
                        (int) (baseColor.getRed() * (1 - ratio * 0.3)),
                        (int) (baseColor.getGreen() * (1 - ratio * 0.3)),
                        (int) (baseColor.getBlue() * (1 - ratio * 0.3)));
                g2d.setColor(segmentColor);
                g2d.fillRect(segment.x + 1, segment.y + 1, unitSize, unitSize);
            }
        }
    }

    // Dessine un rectangle arrondi pour la tête et la queue
    private void drawRoundedRectangle(Graphics2D g2d, int x, int y, int width, int height,
            int arcSize, Direction direction) {
        Path2D path = new Path2D.Float();

        switch (direction) {
            case UP:
                // Arrondi en haut
                path.moveTo(x, y + height);
                path.lineTo(x, y + arcSize);
                path.quadTo(x, y, x + arcSize, y);
                path.lineTo(x + width - arcSize, y);
                path.quadTo(x + width, y, x + width, y + arcSize);
                path.lineTo(x + width, y + height);
                break;
            case DOWN:
                // Arrondi en bas
                path.moveTo(x, y);
                path.lineTo(x, y + height - arcSize);
                path.quadTo(x, y + height, x + arcSize, y + height);
                path.lineTo(x + width - arcSize, y + height);
                path.quadTo(x + width, y + height, x + width, y + height - arcSize);
                path.lineTo(x + width, y);
                break;
            case LEFT:
                // Arrondi à gauche
                path.moveTo(x + width, y);
                path.lineTo(x + arcSize, y);
                path.quadTo(x, y, x, y + arcSize);
                path.lineTo(x, y + height - arcSize);
                path.quadTo(x, y + height, x + arcSize, y + height);
                path.lineTo(x + width, y + height);
                break;
            case RIGHT:
                // Arrondi à droite
                path.moveTo(x, y);
                path.lineTo(x + width - arcSize, y);
                path.quadTo(x + width, y, x + width, y + arcSize);
                path.lineTo(x + width, y + height - arcSize);
                path.quadTo(x + width, y + height, x + width - arcSize, y + height);
                path.lineTo(x, y + height);
                break;
        }
        path.closePath();
        g2d.fill(path);
    }

    // Détermine la direction de la queue basée sur la position relative
    private Direction getTailDirection(Point tail, Point beforeTail) {
        if (tail.x < beforeTail.x)
            return Direction.LEFT;
        if (tail.x > beforeTail.x)
            return Direction.RIGHT;
        if (tail.y < beforeTail.y)
            return Direction.UP;
        return Direction.DOWN;
    }

    private void drawScores(Graphics g, int bordWidth) {
        // Zone de score avec fond semi-transparent
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, bordWidth, 40);

        // Scores
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        String player1Name = model.getPlayer1() != null ? model.getPlayer1().getName() : "Joueur 1";
        String player2Name = model.getPlayer2() != null ? model.getPlayer2().getName() : "Joueur 2";

        g.drawString(player1Name + ": " + model.getPlayer1Score(), 10, 25);
        g.drawString(player2Name + ": " + model.getPlayer2Score(), bordWidth - 150, 25);

        // Longueur des serpents
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.LIGHT_GRAY);
        if (model.getSnake1() != null) {
            g.drawString("Taille: " + model.getSnake1().getLength(), 10, 38);
        }
        if (model.getSnake2() != null) {
            g.drawString("Taille: " + model.getSnake2().getLength(), bordWidth - 150, 38);
        }
    }

    private void drawPauseMenu(Graphics g, int bordWidth, int bordHeight) {
        // Fond semi-transparent
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, bordWidth, bordHeight);

        // Texte PAUSE
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String pauseText = "PAUSE";
        FontMetrics fm = getFontMetrics(g.getFont());
        int x = (bordWidth - fm.stringWidth(pauseText)) / 2;
        g.drawString(pauseText, x, bordHeight / 2 - 50);

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
            x = (bordWidth - fm.stringWidth(instructions[i])) / 2;
            g.drawString(instructions[i], x, bordHeight / 2 + 20 + (i * 30));
        }
    }

    private void drawGameOver(Graphics g, int bordWidth, int bordHeight) {
        // Fond
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, bordWidth, bordHeight);

        // Titre Game Over
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOver = "GAME OVER";
        FontMetrics fm = getFontMetrics(g.getFont());
        int x = (bordWidth - fm.stringWidth(gameOver)) / 2;
        g.drawString(gameOver, x, bordHeight / 2 - 100);

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
        x = (bordWidth - fm.stringWidth(winner)) / 2;
        g.drawString(winner, x, bordHeight / 2);

        // Scores finaux
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        String finalScore = "Score final: " + model.getPlayer1Score() + " - " + model.getPlayer2Score();
        fm = getFontMetrics(g.getFont());
        x = (bordWidth - fm.stringWidth(finalScore)) / 2;
        g.drawString(finalScore, x, bordHeight / 2 + 50);

        // Instruction
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String instruction = "Appuyez sur ESPACE pour retourner au menu";
        fm = getFontMetrics(g.getFont());
        x = (bordWidth - fm.stringWidth(instruction)) / 2;
        g.drawString(instruction, x, bordHeight / 2 + 100);
    }

    private void drawStatusMessages(Graphics g, int bordWidth, int bordHeight) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();

        // Dessiner les messages du plus ancien au plus récent (de bas en haut)
        int yPosition = bordHeight - 80;

        for (int i = statusMessages.size() - 1; i >= 0; i--) {
            StatusMessage msg = statusMessages.get(i);
            float alpha = msg.getAlpha();

            if (alpha > 0) {
                // Appliquer la transparence
                g2d.setColor(new Color(255, 255, 0, (int) (255 * alpha))); // Jaune avec alpha

                int x = (bordWidth - fm.stringWidth(msg.text)) / 2;
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