package org.uit.ui;

import javax.swing.*;

public class HomeFrame extends JFrame {

    private final String username;

    public HomeFrame(String username) {
        super("English Quiz - Home");
        this.username = username;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 360);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(new HomePanel(username, this));

        // Native look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}
    }
}