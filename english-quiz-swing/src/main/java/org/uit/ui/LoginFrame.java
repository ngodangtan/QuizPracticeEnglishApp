package org.uit.ui;

import javax.swing.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        super("English Quiz - Login");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 360);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(new LoginPanel());

        // Native look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}
    }
}
