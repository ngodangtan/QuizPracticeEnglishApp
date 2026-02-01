package org.uit;

import org.uit.navigation.AppCoordinator;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> AppCoordinator.getInstance().showLogin(null));
    }
}