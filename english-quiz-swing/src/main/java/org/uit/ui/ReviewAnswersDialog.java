package org.uit.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Dialog showing quiz review: each question, user's answer, correct answer, and correct/incorrect.
 */
public class ReviewAnswersDialog extends JDialog {

    public ReviewAnswersDialog(Frame owner, List<QuizPanel.Question> questions, int[] userAnswers, Runnable onClose) {
        super(owner, "Xem lại câu trả lời", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (onClose != null) onClose.run();
            }
        });
        setSize(600, 500);
        setLocationRelativeTo(owner);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < questions.size(); i++) {
            QuizPanel.Question q = questions.get(i);
            int userChoice = userAnswers[i];
            boolean correct = (userChoice == q.correctIndex);

            String userText = (userChoice >= 0 && userChoice < 4) ? q.options[userChoice] : "(Không trả lời)";
            String correctText = q.options[q.correctIndex];

            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(correct ? Color.GREEN : Color.RED, 1),
                new EmptyBorder(8, 8, 8, 8)
            ));
            card.setBackground(correct ? new Color(230, 255, 230) : new Color(255, 230, 230));

            JLabel qLabel = new JLabel("<html><b>Question " + (i + 1) + ":</b> " + escapeHtml(q.question) + "</html>");
            qLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(qLabel, BorderLayout.NORTH);

            JPanel answers = new JPanel(new GridLayout(0, 1, 0, 2));
            answers.setOpaque(false);
            answers.add(new JLabel("Your answer: " + userText));
            answers.add(new JLabel("Correct answer: " + correctText));
            answers.add(new JLabel(correct ? "✓ Correct" : "✗ Incorrect"));
            card.add(answers, BorderLayout.CENTER);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        content.add(scroll, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Đóng");
        closeBtn.addActionListener(e -> dispose());
        JPanel south = new JPanel();
        south.add(closeBtn);
        content.add(south, BorderLayout.SOUTH);

        setContentPane(content);
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
