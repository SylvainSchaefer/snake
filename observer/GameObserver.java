package observer;

public interface GameObserver {
    void onScoreUpdate(int player1Score, int player2Score);
    void onGameStateChange(GameState state);
    void onSnakeMove();
    void onAppleEaten(String playerName);
    void onCollision(String playerName);

    // objets spéciaux
    void onGoldenAppleSpawned();
    void onGoldenAppleDisappeared();
    void onGoldenAppleEaten(String playerName);
    void onBombSpawned();
    void onBombDisappeared();
    void onBombHit(String playerName);

    enum GameState {
        MENU, PLAYING, PAUSED, GAME_OVER
    }
}
