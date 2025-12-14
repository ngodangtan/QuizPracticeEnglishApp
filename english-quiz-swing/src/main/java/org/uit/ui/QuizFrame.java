package org.uit.ui;

import org.uit.ApiClient;

import javax.swing.*;

public class QuizFrame extends JFrame {

    private final String username;
    private final String level;
    private final ApiClient.Question[] questions;

    public QuizFrame(String username, String level, ApiClient.Question[] questions) {
        super("English Quiz - Test");
        this.username = username;
        this.level = level;
        this.questions = questions;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(new QuizPanel(username, level, this, questions));

        // Native look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}
    }
}