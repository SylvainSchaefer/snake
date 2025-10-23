package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vue du menu principal
 */
public class MenuView extends JPanel {
    private JButton twoPlayersBtn;
    private JButton vsAIBtn;
    private JButton loadBtn;
    private JButton quitBtn;

    public MenuView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 800));

        initComponents();
    }

    private void initComponents() {
        // Titre
        JLabel titleLabel = createTitle("SNAKE 2 JOUEURS");

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Minel, Sefa, Nicolas et Sylvain");
        subtitleLabel.setForeground(Color.GREEN.darker());
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Boutons
        twoPlayersBtn = createMenuButton("2 Joueurs Local");
        vsAIBtn = createMenuButton("Contre l'IA");
        loadBtn = createMenuButton("Charger Partie");
        quitBtn = createMenuButton("Quitter");

        // Ajout des composants
        add(Box.createVerticalStrut(80));
        add(titleLabel);
        add(Box.createVerticalStrut(10));
        add(subtitleLabel);
        add(Box.createVerticalStrut(60));
        add(twoPlayersBtn);
        add(Box.createVerticalStrut(20));
        add(vsAIBtn);
        add(Box.createVerticalStrut(20));
        add(loadBtn);
        add(Box.createVerticalStrut(20));
        add(quitBtn);
        add(Box.createVerticalGlue());

        // Instructions en bas
        JPanel instructionsPanel = createInstructionsPanel();
        add(instructionsPanel);
        add(Box.createVerticalStrut(20));
    }

    private JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.GREEN);
        label.setFont(new Font("Arial", Font.BOLD, 36));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 45));
        button.setBackground(Color.GREEN.darker());
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));

        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.GREEN);
                button.setForeground(Color.BLACK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.GREEN.darker());
                button.setForeground(Color.WHITE);
            }
        });

        return button;
    }

    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel controlsTitle = new JLabel("CONTRÔLES");
        controlsTitle.setForeground(Color.GRAY);
        controlsTitle.setFont(new Font("Arial", Font.BOLD, 14));
        controlsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] instructions = {
                "Joueur 1: Z Q S D",
                "Joueur 2: Flèches",
                "P: Pause | S: Sauvegarder (en pause)"
        };

        panel.add(controlsTitle);
        panel.add(Box.createVerticalStrut(5));

        for (String instruction : instructions) {
            JLabel label = new JLabel(instruction);
            label.setForeground(Color.GRAY);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(label);
        }

        return panel;
    }

    // Méthodes pour ajouter des listeners
    public void addTwoPlayersListener(ActionListener listener) {
        twoPlayersBtn.addActionListener(listener);
    }

    public void addVsAIListener(ActionListener listener) {
        vsAIBtn.addActionListener(listener);
    }

    public void addLoadListener(ActionListener listener) {
        loadBtn.addActionListener(listener);
    }

    public void addQuitListener(ActionListener listener) {
        quitBtn.addActionListener(listener);
    }
}