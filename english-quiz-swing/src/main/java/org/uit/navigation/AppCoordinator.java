package org.uit.navigation;

import org.uit.ApiClient;
import org.uit.ui.*;

import javax.swing.*;
import java.awt.*;

/**
 * Centralized coordinator for navigating between application screens.
 */
public class AppCoordinator {
    private static final AppCoordinator INSTANCE = new AppCoordinator();

    private AppCoordinator() {}

    public static AppCoordinator getInstance() {
        return INSTANCE;
    }

    public void showLogin(Window from) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame f = new LoginFrame();
            f.setVisible(true);
            if (from != null) from.dispose();
        });
    }

    public void showRegister(Window from) {
        SwingUtilities.invokeLater(() -> {
            RegisterFrame f = new RegisterFrame();
            f.setVisible(true);
            if (from != null) from.dispose();
        });
    }

    public void showHome(String username, Window from) {
        SwingUtilities.invokeLater(() -> {
            HomeFrame f = new HomeFrame(username);
            f.setVisible(true);
            if (from != null) from.dispose();
        });
    }

    public void showLoading(String username, String level, Window from) {
        SwingUtilities.invokeLater(() -> {
            LoadingFrame f = new LoadingFrame(username, level);
            f.setVisible(true);
            if (from != null) from.dispose();
        });
    }

    public void showQuiz(String username, String level, ApiClient.Question[] questions, Window from) {
        SwingUtilities.invokeLater(() -> {
            QuizFrame f = new QuizFrame(username, level, questions);
            f.setVisible(true);
            if (from != null) from.dispose();
        });
    }

    public void showProfile(String username, Window from) {
        SwingUtilities.invokeLater(() -> {
            ProfileFrame f = new ProfileFrame();
            f.setVisible(true);
            if (from != null) from.dispose();
        });
    }
}
