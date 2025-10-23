package observer;

/**
 * Interface Observer pour le pattern Observer
 */
public interface GameObserver {
    void onScoreUpdate(int player1Score, int player2Score);
    void onGameStateChange(GameState state);
    void onSnakeMove();
    void onAppleEaten(String playerName);
    void onCollision(String playerName);
    
    enum GameState {
        MENU, PLAYING, PAUSED, GAME_OVER
    }
}