package org.uit.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JCheckBox showPassword = new JCheckBox("Show password");
    private final JButton loginBtn = new JButton("Login");
    private final JButton registerBtn = new JButton("Create account");
    private final JLabel errorLabel = new JLabel(" "); // giữ chỗ cho layout

    public LoginPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(22, 22, 22, 22));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        wireEvents();
    }

    private JComponent buildHeader() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome back");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JLabel sub = new JLabel("Sign in to start the English quiz");
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
        p.add(labeledField("Email", emailField), g);

        g.gridy = row++;
        p.add(labeledField("Password", passwordField), g);

        // error label
        errorLabel.setForeground(new Color(200, 60, 60));
        errorLabel.setFont(errorLabel.getFont().deriveFont(12f));
        g.gridy = row++;
        g.insets = new Insets(0, 0, 8, 0);
        p.add(errorLabel, g);

        // show password
        showPassword.setOpaque(false);
        g.gridy = row++;
        g.insets = new Insets(0, 0, 8, 0);
        p.add(showPassword, g);

        // buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.add(loginBtn);
        btnRow.add(registerBtn);

        g.gridy = row++;
        g.insets = new Insets(14, 0, 0, 0);
        p.add(btnRow, g);

        return p;
    }

    private JComponent buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel hint = new JLabel("UI only for now (no DB). Next: hook to register + quiz screen.");
        hint.setForeground(new Color(140, 140, 140));
        hint.setFont(hint.getFont().deriveFont(12f));

        p.add(hint, BorderLayout.WEST);
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
        passwordField.addActionListener(e -> loginBtn.doClick());

        showPassword.addActionListener(e -> {
            passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '•');
        });

        loginBtn.addActionListener(e -> {
            clearError();

            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (email.isEmpty() || pass.isEmpty()) {
                setError("Please enter email and password.");
                return;
            }
            if (!email.contains("@")) {
                setError("Email format looks invalid.");
                return;
            }

            JOptionPane.showMessageDialog(this, "Login clicked (UI only).");
        });

        registerBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Go to Register screen (next step).");
        });
    }

    private void setError(String msg) { errorLabel.setText(msg); }
    private void clearError() { errorLabel.setText(" "); }
}
