package org.uit.ui;

import org.uit.ApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

public class LoadingPanel extends JPanel {

    private final String username;
    private final String level;
    private final LoadingFrame loadingFrame;
    private final SpinnerPanel spinnerPanel = new SpinnerPanel();
    private final JLabel statusLabel;

    public LoadingPanel(String username, String level, LoadingFrame loadingFrame) {
        this.username = username;
        this.level = level;
        this.loadingFrame = loadingFrame;
        this.statusLabel = new JLabel("Loading questions for level " + level + "...");

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(22, 22, 22, 22));

        add(buildContent(), BorderLayout.CENTER);

        startLoading();
    }

    private JComponent buildContent() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 16f));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        spinnerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(statusLabel);
        p.add(Box.createVerticalStrut(20));
        p.add(spinnerPanel);
        p.add(Box.createVerticalGlue());

        return p;
    }

    private void startLoading() {
        // Call API to generate quiz
        new SwingWorker<ApiClient.Question[], Void>() {
            @Override
            protected ApiClient.Question[] doInBackground() throws Exception {
                return ApiClient.generateQuiz(level);
            }

            @Override
            protected void done() {
                try {
                    ApiClient.Question[] questions = get();
                    SwingUtilities.invokeLater(() -> {
                        new QuizFrame(username, level, questions).setVisible(true);
                        loadingFrame.dispose();
                    });
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(LoadingPanel.this, "Failed to load quiz: " + ex.getCause().getMessage());
                    // Go back to home
                    SwingUtilities.invokeLater(() -> {
                        new HomeFrame(username).setVisible(true);
                        loadingFrame.dispose();
                    });
                }
            }
        }.execute();
    }

    // Custom spinner component
    private static class SpinnerPanel extends JPanel implements ActionListener {
        private int angle = 0;
        private final Timer timer;

        public SpinnerPanel() {
            setPreferredSize(new Dimension(50, 50));
            setOpaque(false);
            timer = new Timer(50, this); // Update every 50ms
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int radius = Math.min(centerX, centerY) - 5;

            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2, angle, 270);

            g2d.dispose();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            angle = (angle + 10) % 360; // Rotate by 10 degrees
            repaint();
        }
    }
}