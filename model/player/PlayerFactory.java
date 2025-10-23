package model.player;

/**
 * Factory pour créer les différents types de joueurs
 */
public class PlayerFactory {
    
    public static Player createPlayer(Player.PlayerType type, String name) {
        switch (type) {
            case HUMAN:
                return new HumanPlayer(name);
            case AI_EASY:
                return new EasyAI();
            case AI_MEDIUM:
                return new MediumAI();
            case AI_HARD:
                return new HardAI();
            default:
                throw new IllegalArgumentException("Type de joueur inconnu: " + type);
        }
    }
    
    public static Player createHumanPlayer(String name) {
        return createPlayer(Player.PlayerType.HUMAN, name);
    }
    
    public static Player createAIPlayer(int difficulty) {
        switch (difficulty) {
            case 1:
                return createPlayer(Player.PlayerType.AI_EASY, null);
            case 2:
                return createPlayer(Player.PlayerType.AI_MEDIUM, null);
            case 3:
                return createPlayer(Player.PlayerType.AI_HARD, null);
            default:
                throw new IllegalArgumentException("Niveau de difficulté invalide: " + difficulty);
        }
    }
}