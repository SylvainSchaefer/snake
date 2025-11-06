package util;

import observer.GameObserver;
import java.io.*;
import java.util.*;

/**
 * Classe pour gérer les statistiques du jeu (implémente Observer)
 */
public class GameStatistics implements GameObserver, Serializable {
    private static final long serialVersionUID = 1L;
    private static final String STATS_FILE = "snake_statistics.dat";
    
    // Statistiques globales
    private int totalGamesPlayed;
    private int totalApplesEaten;
    private int totalCollisions;
    private Map<String, PlayerStats> playerStats;
    
    // Statistiques de la partie en cours
    private long gameStartTime;
    private int currentGameApples;
    private int currentGameCollisions;
    
    public GameStatistics() {
        playerStats = new HashMap<>();
        loadStatistics();
    }
    
    @Override
    public void onGameStateChange(GameState state) {
        switch(state) {
            case PLAYING:
                gameStartTime = System.currentTimeMillis();
                currentGameApples = 0;
                currentGameCollisions = 0;
                totalGamesPlayed++;
                break;
            case GAME_OVER:
                long gameDuration = System.currentTimeMillis() - gameStartTime;
                System.out.println("Partie terminée - Durée: " + (gameDuration / 1000) + " secondes");
                saveStatistics();
                break;
        }
    }
    
    @Override
    public void onScoreUpdate(int player1Score, int player2Score) {
        // Mettre à jour les scores maximums
        updatePlayerHighScore("Joueur 1", player1Score);
        updatePlayerHighScore("Joueur 2", player2Score);
    }
    
    @Override
    public void onSnakeMove() {
        // Pas de statistique pour les mouvements
    }
    
    @Override
    public void onAppleEaten(String playerName) {
        currentGameApples++;
        totalApplesEaten++;
        
        PlayerStats stats = playerStats.computeIfAbsent(playerName, k -> new PlayerStats(k));
        stats.applesEaten++;
    }
    
    @Override
    public void onCollision(String playerName) {
        currentGameCollisions++;
        totalCollisions++;
        
        PlayerStats stats = playerStats.computeIfAbsent(playerName, k -> new PlayerStats(k));
        stats.collisions++;
    }

    @Override
    public void onGoldenAppleSpawned() {

    }

    @Override
    public void onGoldenAppleDisappeared() {

    }

    @Override
    public void onGoldenAppleEaten(String playerName) {

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

    private void updatePlayerHighScore(String playerName, int score) {
        PlayerStats stats = playerStats.computeIfAbsent(playerName, k -> new PlayerStats(k));
        if (score > stats.highScore) {
            stats.highScore = score;
            stats.highScoreDate = new Date();
        }
    }
    
    public void saveStatistics() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(STATS_FILE))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des statistiques: " + e.getMessage());
        }
    }
    
    public void loadStatistics() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(STATS_FILE))) {
            GameStatistics loaded = (GameStatistics) in.readObject();
            this.totalGamesPlayed = loaded.totalGamesPlayed;
            this.totalApplesEaten = loaded.totalApplesEaten;
            this.totalCollisions = loaded.totalCollisions;
            this.playerStats = loaded.playerStats;
        } catch (IOException | ClassNotFoundException e) {
            // Fichier n'existe pas encore ou erreur - utiliser les valeurs par défaut
            System.out.println("Nouvelles statistiques créées");
        }
    }
    
    public void displayStatistics() {
        System.out.println("\n=== STATISTIQUES GLOBALES ===");
        System.out.println("Parties jouées: " + totalGamesPlayed);
        System.out.println("Pommes mangées: " + totalApplesEaten);
        System.out.println("Collisions totales: " + totalCollisions);
        
        System.out.println("\n=== STATISTIQUES PAR JOUEUR ===");
        for (PlayerStats stats : playerStats.values()) {
            System.out.println(stats);
        }
    }
    
    // Classe interne pour les statistiques d'un joueur
    private static class PlayerStats implements Serializable {
        private static final long serialVersionUID = 1L;
        String playerName;
        int highScore;
        Date highScoreDate;
        int applesEaten;
        int collisions;
        
        PlayerStats(String playerName) {
            this.playerName = playerName;
            this.highScore = 0;
            this.applesEaten = 0;
            this.collisions = 0;
        }
        
        @Override
        public String toString() {
            return String.format("%s - Meilleur score: %d (le %s) - Pommes: %d - Collisions: %d",
                playerName, highScore, 
                highScoreDate != null ? highScoreDate.toString() : "N/A",
                applesEaten, collisions);
        }
    }
    
    // Getters pour l'affichage dans l'interface
    public int getTotalGamesPlayed() { return totalGamesPlayed; }
    public int getTotalApplesEaten() { return totalApplesEaten; }
    public int getTotalCollisions() { return totalCollisions; }
    public Map<String, PlayerStats> getPlayerStats() { return new HashMap<>(playerStats); }
}