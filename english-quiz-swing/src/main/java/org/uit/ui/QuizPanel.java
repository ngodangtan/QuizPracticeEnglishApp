package org.uit.ui;

import org.uit.ApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import org.uit.navigation.AppCoordinator;
import org.uit.session.Session;

public class QuizPanel extends JPanel {

    private final String username;
    private final String level;
    private final QuizFrame quizFrame;

    // Quiz data from API
    private final List<Question> questions = new ArrayList<>();
    private final int[] userAnswers; // 0=A, 1=B, 2=C, 3=D, -1 not answered
    private int currentQuestionIndex = 0;

    // UI components
    private final JLabel timerLabel = new JLabel("Time remaining: 20:00");
    private final JLabel questionLabel = new JLabel();
    private final JRadioButton[] optionButtons = new JRadioButton[4];
    private final ButtonGroup optionGroup = new ButtonGroup();
    private final JButton prevBtn = new JButton("Previous");
    private final JButton nextBtn = new JButton("Next");
    private final JButton submitBtn = new JButton("Submit Quiz");
    private JProgressBar progressBar; // initialized in buildHeader to handle zero-size safely

    private Timer quizTimer;
    private int timeRemaining = 1200; // 20 minutes in seconds

    public QuizPanel(String username, String level, QuizFrame quizFrame, ApiClient.Question[] apiQuestions) {
        this.username = username;
        this.level = level;
        this.quizFrame = quizFrame;

        // Handle empty or null API response
        if (apiQuestions == null || apiQuestions.length == 0) {
            this.userAnswers = new int[0];
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(22, 22, 22, 22));
            JLabel noQuestions = new JLabel("No questions available. Returning to home...");
            noQuestions.setHorizontalAlignment(SwingConstants.CENTER);
            add(noQuestions, BorderLayout.CENTER);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(quizFrame, "No questions available. Returning to home.");
                AppCoordinator.getInstance().showHome(username, quizFrame);
            });
            return;
        }

        // Convert API questions to local Question
        for (ApiClient.Question q : apiQuestions) {
            String[] options = {q.Choice.get("A"), q.Choice.get("B"), q.Choice.get("C"), q.Choice.get("D")};
            int correctIndex = q.Correct.charAt(0) - 'A'; // A=0, B=1, etc.
            questions.add(new Question(q.question, options, correctIndex));
        }

        this.userAnswers = new int[questions.size()];

        // Initialize userAnswers to -1
        for (int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = -1;
        }

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(22, 22, 22, 22));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildQuestionPanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        updateQuestion();
        startTimer();
        wireEvents();
    }

    private JComponent buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        timerLabel.setFont(timerLabel.getFont().deriveFont(Font.BOLD, 18f));
        timerLabel.setForeground(Color.RED);

        int total = Math.max(1, questions.size());
        progressBar = new JProgressBar(0, total);
        progressBar.setValue(Math.min(1, total));
        progressBar.setStringPainted(true);
        progressBar.setString("Question " + (questions.size() == 0 ? 0 : 1) + " of " + questions.size());

        p.add(timerLabel, BorderLayout.WEST);
        p.add(progressBar, BorderLayout.CENTER);

        return p;
    }

    private JComponent buildQuestionPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        questionLabel.setFont(questionLabel.getFont().deriveFont(Font.BOLD, 16f));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(questionLabel);
        p.add(Box.createVerticalStrut(20));

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setOpaque(false);
            optionButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            optionGroup.add(optionButtons[i]);
            p.add(optionButtons[i]);
            p.add(Box.createVerticalStrut(10));
        }

        return p;
    }

    private JComponent buildFooter() {
        JPanel p = new JPanel(new FlowLayout());
        p.setOpaque(false);

        p.add(prevBtn);
        p.add(nextBtn);
        p.add(Box.createHorizontalStrut(50));
        p.add(submitBtn);

        return p;
    }

    private void wireEvents() {
        prevBtn.addActionListener(e -> {
            if (currentQuestionIndex > 0) {
                saveCurrentAnswer();
                currentQuestionIndex--;
                updateQuestion();
            }
        });

        nextBtn.addActionListener(e -> {
            if (currentQuestionIndex < questions.size() - 1) {
                saveCurrentAnswer();
                currentQuestionIndex++;
                updateQuestion();
            }
        });

        submitBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to submit the quiz?", "Submit Quiz", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                submitQuiz();
            }
        });

        // Save answer when radio button selected
        for (int i = 0; i < 4; i++) {
            final int index = i;
            optionButtons[i].addActionListener(e -> userAnswers[currentQuestionIndex] = index);
        }
    }

    private void updateQuestion() {
        if (questions.isEmpty()) return;
        Question q = questions.get(currentQuestionIndex);
        questionLabel.setText((currentQuestionIndex + 1) + ". " + q.question);
        optionGroup.clearSelection();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(q.options[i]);
            if (userAnswers[currentQuestionIndex] == i) {
                optionButtons[i].setSelected(true);
            }
        }
        progressBar.setValue(Math.min(currentQuestionIndex + 1, progressBar.getMaximum()));
        progressBar.setString("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
        prevBtn.setEnabled(currentQuestionIndex > 0);
        nextBtn.setEnabled(currentQuestionIndex < questions.size() - 1);
    }

    private void saveCurrentAnswer() {
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                userAnswers[currentQuestionIndex] = i;
                break;
            }
        }
    }

    private void startTimer() {
        quizTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                timerLabel.setText(String.format("Time remaining: %02d:%02d", minutes, seconds));

                if (timeRemaining <= 0) {
                    quizTimer.stop();
                    timeUp();
                }
            }
        });
        quizTimer.start();
    }

    private void timeUp() {
        JOptionPane.showMessageDialog(this, "Time's up! Submitting quiz automatically.");
        submitQuiz();
    }

    private void submitQuiz() {
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions to submit.");
            goToHome();
            return;
        }

        quizTimer.stop();
        int correct = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers[i] == questions.get(i).correctIndex) {
                correct++;
            }
        }
        double percentage = (double) correct / questions.size() * 100;
        final int correctFinal = correct;
        final double percentageFinal = percentage;

        // Submit score to API (async), then show result when done
        String token = Session.getToken();
        if (token != null && !token.isEmpty()) {
            new SwingWorker<ApiClient.SubmitScoreResponse, Void>() {
                @Override
                protected ApiClient.SubmitScoreResponse doInBackground() throws Exception {
                    return ApiClient.submitScore(percentageFinal, token);
                }

                @Override
                protected void done() {
                    try {
                        ApiClient.SubmitScoreResponse res = get();
                        if (res != null && res.success && res.recentScore != null) {
                            Session.setRecentScore(res.recentScore);
                        }
                    } catch (Exception ignored) {
                        Session.setRecentScore(String.format("%.0f%%", percentageFinal));
                    }
                    showQuizResult(correctFinal, percentageFinal);
                }
            }.execute();
        } else {
            Session.setRecentScore(String.format("%.0f%%", percentage));
            showQuizResult(correct, percentage);
        }
    }

    private void showQuizResult(int correct, double percentage) {
        String message = "Quiz completed!\nCorrect answers: " + correct + "/" + questions.size() + "\nScore: " + String.format("%.1f", percentage) + "%";
        String[] options = { "Xem lại câu trả lời", "Về trang chủ" };
        int choice = JOptionPane.showOptionDialog(this, message, "Quiz completed", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
        if (choice == 0) {
            ReviewAnswersDialog review = new ReviewAnswersDialog(quizFrame, questions, userAnswers, this::goToHome);
            review.setVisible(true);
        } else {
            goToHome();
        }
    }

    private void goToHome() {
        SwingUtilities.invokeLater(() -> {
            AppCoordinator.getInstance().showHome(username, quizFrame);
        });
    }

    /** Used by QuizPanel and ReviewAnswersDialog. */
    public static class Question {
        String question;
        String[] options;
        int correctIndex;

        Question(String q, String[] opts, int correct) {
            this.question = q;
            this.options = opts;
            this.correctIndex = correct;
        }
    }
}