package org.uit.controller;

import org.uit.ApiClient;
import org.uit.api.ApiService;
import org.uit.api.DefaultApiService;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class QuizController {

    public interface QuizListener {
        void onStart();
        void onComplete();
        void onSuccess(ApiClient.Question[] questions);
        void onError(String message);
    }

    private final ApiService apiService;

    public QuizController() { this(new DefaultApiService()); }

    public QuizController(ApiService apiService) { this.apiService = apiService; }

    public void generateQuiz(String level, QuizListener listener) {
        listener.onStart();

        new SwingWorker<ApiClient.Question[], Void>() {
            @Override
            protected ApiClient.Question[] doInBackground() throws Exception {
                return apiService.generateQuiz(level);
            }

            @Override
            protected void done() {
                listener.onComplete();
                try {
                    ApiClient.Question[] qs = get();
                    listener.onSuccess(qs);
                } catch (InterruptedException | ExecutionException ex) {
                    listener.onError("Failed to load quiz: " + ex.getCause().getMessage());
                }
            }
        }.execute();
    }
}
