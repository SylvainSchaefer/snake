package controller;

import model.*;
import model.player.*;
import view.*;
import observer.GameObserver;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Contrôleur principal - gère la logique entre le modèle et les vues
 */
public class GameController {
    private static final int GAME_DELAY = 100;

    private MainWindow mainWindow;
    private MenuView menuView;
    private DifficultyView difficultyView;
    private GameView gameView;
    private GameModel gameModel;
    private Timer gameTimer;  // logique
    private Timer renderTimer; // rendu

    public GameController() {
        mainWindow = new MainWindow();
        menuView = new MenuView();
        difficultyView = new DifficultyView();

        mainWindow.addView(menuView, "menu");
        mainWindow.addView(difficultyView, "difficulty");

        setupMenuListeners();
        setupDifficultyListeners();
    }

    public void start() {
        mainWindow.showView("menu");
        mainWindow.display();
    }

    private void setupMenuListeners() {
        menuView.addTwoPlayersListener(e -> startTwoPlayerGame());
        menuView.addVsAIListener(e -> showDifficultySelection());
        menuView.addLoadListener(e -> loadGame());
        menuView.addQuitListener(e -> System.exit(0));
    }

    private void setupDifficultyListeners() {
        difficultyView.addEasyListener(e -> startAIGame(1));
        difficultyView.addMediumListener(e -> startAIGame(2));
        difficultyView.addHardListener(e -> startAIGame(3));
        difficultyView.addBackListener(e -> mainWindow.showView("menu"));
    }

    private void startTwoPlayerGame() {
        Player player1 = PlayerFactory.createHumanPlayer("Joueur 1");
        Player player2 = PlayerFactory.createHumanPlayer("Joueur 2");
        startGame(player1, player2);
    }

    private void startAIGame(int difficulty) {
        Player player1 = PlayerFactory.createHumanPlayer("Joueur 1");
        Player player2 = PlayerFactory.createAIPlayer(difficulty);
        startGame(player1, player2);
    }

    private void showDifficultySelection() {
        mainWindow.showView("difficulty");
    }

    private void startGame(Player player1, Player player2) {
        gameModel = new GameModel();
        gameView = new GameView(gameModel);
        mainWindow.addView(gameView, "game");

        setupGameControls();
        gameModel.initGame(player1, player2);

        // Observateur du jeu
        gameModel.addObserver(new GameObserver() {
            @Override public void onGameStateChange(GameState state) {
                if (state == GameState.GAME_OVER) stopGame();
            }
            @Override public void onScoreUpdate(int s1, int s2) {}
            @Override public void onSnakeMove() {}
            @Override public void onAppleEaten(String n) {}
            @Override public void onCollision(String n) {}
            @Override public void onGoldenAppleSpawned() {}
            @Override public void onGoldenAppleDisappeared() {}
            @Override public void onGoldenAppleEaten(String n) {}
            @Override public void onBombSpawned() {}
            @Override public void onBombDisappeared() {}
            @Override public void onBombHit(String n) {}
        });

        // Timer logique (10 FPS)
        gameTimer = new Timer(GAME_DELAY, e -> {
            if (gameModel != null) {
                gameModel.update();
            }
        });
        gameTimer.setCoalesce(false);
        gameTimer.start();

        // Timer rendu (60 FPS)
        renderTimer = new Timer(16, e -> {
            if (gameView != null) gameView.repaint();
        });
        renderTimer.setCoalesce(false);
        renderTimer.start();

        mainWindow.showView("game");
        gameView.requestFocusInWindow();
    }

    private void setupGameControls() {
        gameView.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });
    }

    private void handleKeyPress(int keyCode) {
        if (gameModel == null) return;

        switch (keyCode) {
            // Joueur 1 (ZQSD)
            case KeyEvent.VK_Z -> gameModel.setPlayer1Direction(Direction.UP);
            case KeyEvent.VK_S -> {
                if (gameModel.isPaused()) saveGame();
                else gameModel.setPlayer1Direction(Direction.DOWN);
            }
            case KeyEvent.VK_Q -> gameModel.setPlayer1Direction(Direction.LEFT);
            case KeyEvent.VK_D -> gameModel.setPlayer1Direction(Direction.RIGHT);

            // Joueur 2 (Flèches)
            case KeyEvent.VK_UP -> {
                if (gameModel.getPlayer2() instanceof HumanPlayer)
                    gameModel.setPlayer2Direction(Direction.UP);
            }
            case KeyEvent.VK_DOWN -> {
                if (gameModel.getPlayer2() instanceof HumanPlayer)
                    gameModel.setPlayer2Direction(Direction.DOWN);
            }
            case KeyEvent.VK_LEFT -> {
                if (gameModel.getPlayer2() instanceof HumanPlayer)
                    gameModel.setPlayer2Direction(Direction.LEFT);
            }
            case KeyEvent.VK_RIGHT -> {
                if (gameModel.getPlayer2() instanceof HumanPlayer)
                    gameModel.setPlayer2Direction(Direction.RIGHT);
            }

            // Commandes générales
            case KeyEvent.VK_P -> gameModel.togglePause();
            case KeyEvent.VK_ESCAPE -> { if (gameModel.isPaused()) returnToMenu(); }
            case KeyEvent.VK_SPACE -> { if (!gameModel.isRunning()) returnToMenu(); }
        }
    }

    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("snake_save.dat"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) { return f.getName().endsWith(".dat") || f.isDirectory(); }
            public String getDescription() { return "Fichiers de sauvegarde Snake (*.dat)"; }
        });

        if (fileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile().getPath();
                if (!filename.endsWith(".dat")) filename += ".dat";
                SaveState saveState = new SaveState(gameModel);
                saveState.save(filename);
                JOptionPane.showMessageDialog(mainWindow, "Partie sauvegardée avec succès!", "Sauvegarde", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainWindow, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) { return f.getName().endsWith(".dat") || f.isDirectory(); }
            public String getDescription() { return "Fichiers de sauvegarde Snake (*.dat)"; }
        });

        if (fileChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile().getPath();
                SaveState saveState = SaveState.load(filename);

                gameModel = new GameModel();
                gameView = new GameView(gameModel);
                saveState.restoreToModel(gameModel);
                mainWindow.addView(gameView, "game");
                setupGameControls();

                // Timer logique
                gameTimer = new Timer(GAME_DELAY, e -> { if (gameModel != null) gameModel.update(); });
                gameTimer.start();

                // Timer rendu
                renderTimer = new Timer(16, e -> { if (gameView != null) gameView.repaint(); });
                renderTimer.start();

                mainWindow.showView("game");
                gameView.requestFocusInWindow();

                JOptionPane.showMessageDialog(mainWindow, "Partie chargée avec succès!", "Chargement", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainWindow, "Erreur lors du chargement: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void stopGame() {
        if (gameTimer != null) gameTimer.stop();
        if (renderTimer != null) renderTimer.stop();
    }

    private void returnToMenu() {
        stopGame();
        mainWindow.showView("menu");
    }
}
