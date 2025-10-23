package observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite Observable pour implémenter le pattern Observer
 */
public abstract class Observable {
    private List<GameObserver> observers = new ArrayList<>();
    
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }
    
    protected void notifyScoreUpdate(int player1Score, int player2Score) {
        for (GameObserver observer : observers) {
            observer.onScoreUpdate(player1Score, player2Score);
        }
    }
    
    protected void notifyGameStateChange(GameObserver.GameState state) {
        for (GameObserver observer : observers) {
            observer.onGameStateChange(state);
        }
    }
    
    protected void notifySnakeMove() {
        for (GameObserver observer : observers) {
            observer.onSnakeMove();
        }
    }
    
    protected void notifyAppleEaten(String playerName) {
        for (GameObserver observer : observers) {
            observer.onAppleEaten(playerName);
        }
    }
    
    protected void notifyCollision(String playerName) {
        for (GameObserver observer : observers) {
            observer.onCollision(playerName);
        }
    }
}