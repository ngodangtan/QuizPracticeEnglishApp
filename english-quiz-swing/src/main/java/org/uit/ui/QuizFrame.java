package org.uit.ui;

import javax.swing.*;

public class QuizFrame extends JFrame {

    private final String username;
    private final String level;

    public QuizFrame(String username, String level) {
        super("English Quiz - Test");
        this.username = username;
        this.level = level;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(new QuizPanel(username, level, this));

        // Native look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}
    }
}