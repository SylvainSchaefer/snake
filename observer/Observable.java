package observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable {
    private final List<GameObserver> observers = new ArrayList<>();

    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    protected void notifyScoreUpdate(int player1Score, int player2Score) {
        for (GameObserver o : observers) o.onScoreUpdate(player1Score, player2Score);
    }

    protected void notifyGameStateChange(GameObserver.GameState state) {
        for (GameObserver o : observers) o.onGameStateChange(state);
    }

    protected void notifySnakeMove() {
        for (GameObserver o : observers) o.onSnakeMove();
    }

    protected void notifyAppleEaten(String playerName) {
        for (GameObserver o : observers) o.onAppleEaten(playerName);
    }

    protected void notifyCollision(String playerName) {
        for (GameObserver o : observers) o.onCollision(playerName);
    }

    // === ajouts pour les objets spéciaux ===
    protected void notifyGoldenAppleSpawned() {
        for (GameObserver o : observers) o.onGoldenAppleSpawned();
    }

    protected void notifyGoldenAppleDisappeared() {
        for (GameObserver o : observers) o.onGoldenAppleDisappeared();
    }

    protected void notifyGoldenAppleEaten(String playerName) {
        for (GameObserver o : observers) o.onGoldenAppleEaten(playerName);
    }

    protected void notifyBombSpawned() {
        for (GameObserver o : observers) o.onBombSpawned();
    }

    protected void notifyBombDisappeared() {
        for (GameObserver o : observers) o.onBombDisappeared();
    }

    protected void notifyBombHit(String playerName) {
        for (GameObserver o : observers) o.onBombHit(playerName);
    }
}
