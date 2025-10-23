package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vue pour la sélection de la difficulté de l'IA
 */
public class DifficultyView extends JPanel {
    private JButton easyBtn;
    private JButton mediumBtn;
    private JButton hardBtn;
    private JButton backBtn;
    
    public DifficultyView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 800));
        
        initComponents();
    }
    
    private void initComponents() {
        // Titre
        JLabel titleLabel = new JLabel("Choisir la difficulté de l'IA");
        titleLabel.setForeground(Color.GREEN);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description des difficultés
        JPanel descriptionsPanel = createDescriptionsPanel();
        
        // Boutons
        easyBtn = createDifficultyButton("FACILE", Color.GREEN);
        mediumBtn = createDifficultyButton("MOYEN", Color.ORANGE);
        hardBtn = createDifficultyButton("DIFFICILE", Color.RED);
        backBtn = createBackButton();
        
        // Ajout des composants
        add(Box.createVerticalStrut(100));
        add(titleLabel);
        add(Box.createVerticalStrut(40));
        add(descriptionsPanel);
        add(Box.createVerticalStrut(40));
        add(easyBtn);
        add(Box.createVerticalStrut(20));
        add(mediumBtn);
        add(Box.createVerticalStrut(20));
        add(hardBtn);
        add(Box.createVerticalStrut(40));
        add(backBtn);
        add(Box.createVerticalGlue());
    }
    
    private JPanel createDescriptionsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new GridLayout(3, 1, 0, 10));
        panel.setMaximumSize(new Dimension(400, 100));
        
        addDifficultyDescription(panel, "Facile", "L'IA fait des erreurs fréquentes (30% précision)", Color.GREEN);
        addDifficultyDescription(panel, "Moyen", "L'IA est compétente (60% précision)", Color.ORANGE);
        addDifficultyDescription(panel, "Difficile", "L'IA est redoutable (90% précision + pathfinding)", Color.RED);
        
        return panel;
    }
    
    private void addDifficultyDescription(JPanel panel, String level, String description, Color color) {
        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        descPanel.setBackground(Color.BLACK);
        
        JLabel levelLabel = new JLabel(level + ": ");
        levelLabel.setForeground(color);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.LIGHT_GRAY);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        descPanel.add(levelLabel);
        descPanel.add(descLabel);
        panel.add(descPanel);
    }
    
    private JButton createDifficultyButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 45));
        button.setBackground(color.darker());
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(color, 2));
        
        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    private JButton createBackButton() {
        JButton button = new JButton("← Retour");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(150, 35));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        return button;
    }
    
    // Méthodes pour ajouter des listeners
    public void addEasyListener(ActionListener listener) {
        easyBtn.addActionListener(listener);
    }
    
    public void addMediumListener(ActionListener listener) {
        mediumBtn.addActionListener(listener);
    }
    
    public void addHardListener(ActionListener listener) {
        hardBtn.addActionListener(listener);
    }
    
    public void addBackListener(ActionListener listener) {
        backBtn.addActionListener(listener);
    }
}