package org.uit.ui;

import javax.swing.*;

public class LoadingFrame extends JFrame {

    private final String username;
    private final String level;

    public LoadingFrame(String username, String level) {
        super("English Quiz - Loading");
        this.username = username;
        this.level = level;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(new LoadingPanel(username, level, this));

        // Native look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}
    }
}