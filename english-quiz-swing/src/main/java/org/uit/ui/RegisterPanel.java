package org.uit.ui;

import org.uit.ApiClient;
import org.uit.controller.RegisterController;
import org.uit.navigation.AppCoordinator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public class RegisterPanel extends JPanel {

    private final JTextField usernameField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JCheckBox showPassword = new JCheckBox("Show password");

    private final JButton createBtn = new JButton("Create account");
    private final JButton backBtn = new JButton("Back to login");
    private final JLabel errorLabel = new JLabel(" ");
    private final CircularProgressBar loadingSpinner = new CircularProgressBar();

    public RegisterPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(22, 22, 22, 22));

        loadingSpinner.setVisible(false);

        add(buildHeader(), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(buildForm());
        scroll.setBorder(null);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);

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
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(labeledField("Username", usernameField));
        p.add(Box.createVerticalStrut(8));
        p.add(labeledField("Full name", fullNameField));
        p.add(Box.createVerticalStrut(8));
        p.add(labeledField("Email", emailField));
        p.add(Box.createVerticalStrut(8));
        p.add(labeledField("Password", passwordField));
        p.add(Box.createVerticalStrut(8));

        errorLabel.setForeground(new Color(200, 60, 60));
        errorLabel.setFont(errorLabel.getFont().deriveFont(12f));
        p.add(errorLabel);
        p.add(Box.createVerticalStrut(8));

        showPassword.setOpaque(false);
        p.add(showPassword);
        p.add(Box.createVerticalStrut(8));

        p.add(loadingSpinner);
        p.add(Box.createVerticalStrut(14));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRow.setOpaque(false);
        btnRow.add(backBtn);
        btnRow.add(createBtn);
        p.add(btnRow);

        return p;
    }

    private JPanel labeledField(String label, JComponent field) {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        JLabel l = new JLabel(label);
        l.setFont(l.getFont().deriveFont(13f));

        field.setPreferredSize(new Dimension(200, 34));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

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
            Window w = SwingUtilities.getWindowAncestor(this);
            AppCoordinator.getInstance().showLogin(w);
        });

        createBtn.addActionListener(e -> {
            clearError();

            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                setError("Please fill in all fields.");
                return;
            }
            if (username.contains(" ")) {
                setError("Username must not contain spaces.");
                return;
            }
            if (!email.contains("@")) {
                setError("Email format looks invalid.");
                return;
            }
            if (pass.length() < 6) {
                setError("Password must be at least 6 characters.");
                return;
            }

            // Show loading and disable button
            loadingSpinner.setVisible(true);
            loadingSpinner.start();
            createBtn.setEnabled(false);

            // Call API via controller (MVC)
            RegisterController controller = new RegisterController();
            controller.register(fullName, username, email, pass, new RegisterController.RegisterListener() {
                @Override
                public void onStart() { /* already started */ }

                @Override
                public void onComplete() {
                    loadingSpinner.setVisible(false);
                    loadingSpinner.stop();
                    createBtn.setEnabled(true);
                }

                @Override
                public void onSuccess(ApiClient.RegisterResponse.User user) {
                    Window w = SwingUtilities.getWindowAncestor(RegisterPanel.this);
                    AppCoordinator.getInstance().showLogin(w);
                }

                @Override
                public void onError(String message) {
                    setError(message);
                }
            });
        });
    }

    private void setError(String msg) { errorLabel.setText(msg); }
    private void clearError() { errorLabel.setText(" "); }
}
