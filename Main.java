import javax.swing.SwingUtilities;
import controller.GameController;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.start();
        });
    }
}