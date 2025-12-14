package org.uit.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterPanel extends JPanel {

    private final JTextField usernameField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JCheckBox showPassword = new JCheckBox("Show password");

    private final JButton createBtn = new JButton("Create account");
    private final JButton backBtn = new JButton("Back to login");
    private final JLabel errorLabel = new JLabel(" ");

    public RegisterPanel() {
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

        JLabel title = new JLabel("Create your account");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JLabel sub = new JLabel("Enter username, full name and password");
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
        p.add(labeledField("Username", usernameField), g);

        g.gridy = row++;
        p.add(labeledField("Full name", fullNameField), g);

        g.gridy = row++;
        p.add(labeledField("Password", passwordField), g);

        errorLabel.setForeground(new Color(200, 60, 60));
        errorLabel.setFont(errorLabel.getFont().deriveFont(12f));
        g.gridy = row++;
        g.insets = new Insets(0, 0, 8, 0);
        p.add(errorLabel, g);

        showPassword.setOpaque(false);
        g.gridy = row++;
        g.insets = new Insets(0, 0, 8, 0);
        p.add(showPassword, g);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.add(backBtn);
        btnRow.add(createBtn);

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
        passwordField.addActionListener(e -> createBtn.doClick());

        showPassword.addActionListener(e ->
                passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '\u2022')
        );

        backBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
                Window w = SwingUtilities.getWindowAncestor(this);
                if (w != null) w.dispose();
            });
        });

        createBtn.addActionListener(e -> {
            clearError();

            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || fullName.isEmpty() || pass.isEmpty()) {
                setError("Please fill in all fields.");
                return;
            }
            if (username.contains(" ")) {
                setError("Username must not contain spaces.");
                return;
            }
            if (pass.length() < 6) {
                setError("Password must be at least 6 characters.");
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Account created (UI only)\n\nUsername: " + username +
                            "\nFull name: " + fullName,
                    "Register",
                    JOptionPane.INFORMATION_MESSAGE);

            new LoginFrame().setVisible(true);
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.dispose();
        });
    }

    private void setError(String msg) { errorLabel.setText(msg); }
    private void clearError() { errorLabel.setText(" "); }
}
