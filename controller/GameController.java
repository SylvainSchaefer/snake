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
    private Timer gameTimer; // Pour la logique (100ms)
    private Timer renderTimer; // Pour le rendu (16ms ≈ 60 FPS)

    public GameController() {
        // Initialiser la fenêtre principale
        mainWindow = new MainWindow();

        // Initialiser les vues
        menuView = new MenuView();
        difficultyView = new DifficultyView();

        // Ajouter les vues à la fenêtre principale
        mainWindow.addView(menuView, "menu");
        mainWindow.addView(difficultyView, "difficulty");

        // Configurer les listeners
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
        // Créer un nouveau modèle et vue de jeu
        gameModel = new GameModel();
        gameView = new GameView(gameModel);

        // Ajouter la vue de jeu
        mainWindow.addView(gameView, "game");

        // Configurer les contrôles
        setupGameControls();

        // Initialiser le jeu
        gameModel.initGame(player1, player2, gameView.getWidth(), gameView.getHeight());

        // Observer pour détecter la fin du jeu
        gameModel.addObserver(new GameObserver() {
            @Override
            public void onGameStateChange(GameState state) {
                if (state == GameState.GAME_OVER) {
                    stopGame();
                }
            }

            @Override
            public void onScoreUpdate(int player1Score, int player2Score) {
            }

            @Override
            public void onSnakeMove() {
            }

            @Override
            public void onAppleEaten(String playerName) {
            }

            @Override
            public void onCollision(String playerName) {
            }
        });

        // Timer pour la LOGIQUE du jeu (10 FPS)
        gameTimer = new Timer(GAME_DELAY, e -> {
            if (gameModel != null) {
                gameModel.update(gameView.getWidth(), gameView.getHeight());
            }
        });
        gameTimer.setCoalesce(false);
        gameTimer.start();

        // Timer pour le RENDU (60 FPS)
        renderTimer = new Timer(16, e -> {
            if (gameView != null) {
                gameView.repaint();
            }
        });
        renderTimer.setCoalesce(false);
        renderTimer.start();

        // Afficher la vue de jeu
        mainWindow.showView("game");
        gameView.requestFocusInWindow();
    }

    private void setupGameControls() {
        gameView.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });
    }

    private void handleKeyPress(int keyCode) {
        if (gameModel == null)
            return;

        switch (keyCode) {
            // Contrôles Joueur 1 (ZQSD)
            case KeyEvent.VK_Z:
                gameModel.setPlayer1Direction(Direction.UP);
                break;
            case KeyEvent.VK_S:
                if (gameModel.isPaused()) {
                    saveGame();
                } else {
                    gameModel.setPlayer1Direction(Direction.DOWN);
                }
                break;
            case KeyEvent.VK_Q:
                gameModel.setPlayer1Direction(Direction.LEFT);
                break;
            case KeyEvent.VK_D:
                gameModel.setPlayer1Direction(Direction.RIGHT);
                break;

            // Contrôles Joueur 2 (Flèches) - seulement si c'est un humain
            case KeyEvent.VK_UP:
                if (gameModel.getPlayer2() instanceof HumanPlayer) {
                    gameModel.setPlayer2Direction(Direction.UP);
                }
                break;
            case KeyEvent.VK_DOWN:
                if (gameModel.getPlayer2() instanceof HumanPlayer) {
                    gameModel.setPlayer2Direction(Direction.DOWN);
                }
                break;
            case KeyEvent.VK_LEFT:
                if (gameModel.getPlayer2() instanceof HumanPlayer) {
                    gameModel.setPlayer2Direction(Direction.LEFT);
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (gameModel.getPlayer2() instanceof HumanPlayer) {
                    gameModel.setPlayer2Direction(Direction.RIGHT);
                }
                break;

            // Commandes générales
            case KeyEvent.VK_P:
                gameModel.togglePause();
                break;

            case KeyEvent.VK_ESCAPE:
                if (gameModel.isPaused()) {
                    returnToMenu();
                }
                break;

            case KeyEvent.VK_SPACE:
                if (!gameModel.isRunning()) {
                    returnToMenu();
                }
                break;
        }
    }

    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("snake_save.dat"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".dat") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Fichiers de sauvegarde Snake (*.dat)";
            }
        });

        if (fileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile().getPath();
                if (!filename.endsWith(".dat")) {
                    filename += ".dat";
                }

                SaveState saveState = new SaveState(gameModel);
                saveState.save(filename);

                JOptionPane.showMessageDialog(mainWindow,
                        "Partie sauvegardée avec succès!",
                        "Sauvegarde",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainWindow,
                        "Erreur lors de la sauvegarde: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".dat") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Fichiers de sauvegarde Snake (*.dat)";
            }
        });

        if (fileChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile().getPath();
                SaveState saveState = SaveState.load(filename);

                // Créer un nouveau modèle et restaurer l'état
                gameModel = new GameModel();
                gameView = new GameView(gameModel);
                saveState.restoreToModel(gameModel);

                // Ajouter la vue et configurer
                mainWindow.addView(gameView, "game");
                setupGameControls();

                // Observer pour la fin du jeu
                gameModel.addObserver(new GameObserver() {
                    @Override
                    public void onGameStateChange(GameState state) {
                        if (state == GameState.GAME_OVER) {
                            stopGame();
                        }
                    }

                    @Override
                    public void onScoreUpdate(int player1Score, int player2Score) {
                    }

                    @Override
                    public void onSnakeMove() {
                    }

                    @Override
                    public void onAppleEaten(String playerName) {
                    }

                    @Override
                    public void onCollision(String playerName) {
                    }
                });

                // Timer pour la LOGIQUE du jeu (10 FPS)
                gameTimer = new Timer(GAME_DELAY, e -> {
                    if (gameModel != null) {
                        gameModel.update(gameView.getWidth(), gameView.getHeight());
                    }
                });
                gameTimer.setCoalesce(false);
                gameTimer.start();

                // Timer pour le RENDU (60 FPS)
                renderTimer = new Timer(16, e -> {
                    if (gameView != null) {
                        gameView.repaint();
                    }
                });
                renderTimer.setCoalesce(false);
                renderTimer.start();

                // Afficher le jeu
                mainWindow.showView("game");
                gameView.requestFocusInWindow();

                JOptionPane.showMessageDialog(mainWindow,
                        "Partie chargée avec succès!",
                        "Chargement",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainWindow,
                        "Erreur lors du chargement: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (renderTimer != null) {
            renderTimer.stop();
        }
    }

    private void returnToMenu() {
        stopGame();
        mainWindow.showView("menu");
    }

}