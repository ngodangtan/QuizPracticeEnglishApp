package org.uit.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CircularProgressBar extends JComponent {
    private int angle = 0;
    private final Timer timer;

    public CircularProgressBar() {
        setPreferredSize(new Dimension(20, 20));
        setOpaque(false);

        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                angle = (angle + 10) % 360;
                repaint();
            }
        });
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.GRAY);
        g2d.drawArc(x, y, size, size, 0, 360);

        g2d.setColor(Color.BLUE);
        g2d.drawArc(x, y, size, size, angle, 90);

        g2d.dispose();
    }
}