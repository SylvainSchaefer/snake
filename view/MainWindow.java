package view;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre principale de l'application
 */
public class MainWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainWindow() {
        setTitle("Snake 2 Joueurs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 800));
        setResizable(true);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.BLACK);

        add(mainPanel);

        // Centrer la fenêtre
        pack();
        setLocationRelativeTo(null);
    }

    public void addView(Component view, String name) {
        mainPanel.add(view, name);
        pack();
    }

    public void showView(String name) {
        cardLayout.show(mainPanel, name);
    }

    public void display() {
        setVisible(true);
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}