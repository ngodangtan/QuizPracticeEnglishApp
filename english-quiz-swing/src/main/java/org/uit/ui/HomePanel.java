package org.uit.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomePanel extends JPanel {

    private final String username;
    private final HomeFrame homeFrame;
    private final JTextField levelField = new JTextField();
    private final JButton startTestBtn = new JButton("Start Test");
    private final JButton logoutBtn = new JButton("Logout");

    public HomePanel(String username, HomeFrame homeFrame) {
        this.username = username;
        this.homeFrame = homeFrame;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(22, 22, 22, 22));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);

        wireEvents();
    }

    private JComponent buildHeader() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome, " + username);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JLabel sub = new JLabel("Select your English level to start the quiz");
        sub.setForeground(new Color(120, 120, 120));
        sub.setFont(sub.getFont().deriveFont(13f));

        p.add(title);
        p.add(Box.createVerticalStrut(6));
        p.add(sub);
        p.add(Box.createVerticalStrut(16));
        return p;
    }

    private JComponent buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 0, 8, 0);

        int row = 0;

        g.gridy = row++;
        p.add(labeledField("English Level (e.g., A1, B2, C1)", levelField), g);

        // buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.add(startTestBtn);
        btnRow.add(logoutBtn);

        g.gridy = row++;
        g.insets = new Insets(14, 0, 0, 0);
        p.add(btnRow, g);

        return p;
    }

    private JPanel labeledField(String label, JComponent field) {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        JLabel l = new JLabel(label);
        l.setFont(l.getFont().deriveFont(13f));

        field.setPreferredSize(new Dimension(10, 34));

        wrap.add(l);
        wrap.add(Box.createVerticalStrut(6));
        wrap.add(field);
        return wrap;
    }

    private void wireEvents() {
        startTestBtn.addActionListener(e -> {
            String level = levelField.getText().trim();
            if (level.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your English level.");
                return;
            }
            // Open loading screen
            SwingUtilities.invokeLater(() -> {
                new LoadingFrame(username, level).setVisible(true);
                homeFrame.dispose();
            });
        });

        logoutBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
                homeFrame.dispose();
            });
        });
    }
}
