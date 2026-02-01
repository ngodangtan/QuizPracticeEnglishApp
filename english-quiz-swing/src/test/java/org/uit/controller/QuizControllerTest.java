package org.uit.controller;

import org.junit.jupiter.api.Test;
import org.uit.ApiClient;
import org.uit.api.ApiService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class QuizControllerTest {

    @Test
    public void testGenerateQuizSuccess() throws Exception {
        ApiService fake = new ApiService() {
            @Override public ApiClient.LoginResponse login(String identifier, String password) { throw new UnsupportedOperationException(); }
            @Override public ApiClient.RegisterResponse register(String fullName, String username, String email, String password) { throw new UnsupportedOperationException(); }
            @Override public ApiClient.Question[] generateQuiz(String level) {
                ApiClient.Question q = new ApiClient.Question();
                q.question = "q?";
                q.Choice = java.util.Map.of("A","a","B","b","C","c","D","d");
                q.Correct = "A";
                return new ApiClient.Question[] { q };
            }
        };

        QuizController ctrl = new QuizController(fake);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ApiClient.Question[]> out = new AtomicReference<>();

        ctrl.generateQuiz("A1", new QuizController.QuizListener() {
            @Override public void onStart() { }
            @Override public void onComplete() { }
            @Override public void onSuccess(ApiClient.Question[] questions) { out.set(questions); latch.countDown(); }
            @Override public void onError(String message) { latch.countDown(); }
        });

        boolean ok = latch.await(2, TimeUnit.SECONDS);
        assertTrue(ok);
        assertNotNull(out.get());
        assertEquals(1, out.get().length);
        assertEquals("q?", out.get()[0].question);
    }

    @Test
    public void testGenerateQuizError() throws Exception {
        ApiService fake = new ApiService() {
            @Override public ApiClient.LoginResponse login(String identifier, String password) { throw new UnsupportedOperationException(); }
            @Override public ApiClient.RegisterResponse register(String fullName, String username, String email, String password) { throw new UnsupportedOperationException(); }
            @Override public ApiClient.Question[] generateQuiz(String level) { throw new RuntimeException("no server"); }
        };

        QuizController ctrl = new QuizController(fake);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> err = new AtomicReference<>();

        ctrl.generateQuiz("A1", new QuizController.QuizListener() {
            @Override public void onStart() { }
            @Override public void onComplete() { }
            @Override public void onSuccess(ApiClient.Question[] questions) { latch.countDown(); }
            @Override public void onError(String message) { err.set(message); latch.countDown(); }
        });

        boolean ok = latch.await(2, TimeUnit.SECONDS);
        assertTrue(ok);
        assertTrue(err.get().contains("no server") || err.get().toLowerCase().contains("failed"));
    }
}
