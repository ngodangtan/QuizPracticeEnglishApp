package org.uit.controller;

import org.junit.jupiter.api.Test;
import org.uit.ApiClient;
import org.uit.api.ApiService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterControllerTest {

    @Test
    public void testRegisterSuccess() throws Exception {
        ApiService fake = new ApiService() {
            @Override
            public ApiClient.LoginResponse login(String identifier, String password) { throw new UnsupportedOperationException(); }

            @Override
            public ApiClient.RegisterResponse register(String fullName, String username, String email, String password) {
                ApiClient.RegisterResponse res = new ApiClient.RegisterResponse();
                res.success = true;
                res.message = "ok";
                ApiClient.RegisterResponse.User u = new ApiClient.RegisterResponse.User();
                u._id = "u1";
                u.username = username;
                u.fullName = fullName;
                u.email = email;
                u.createdAt = "now";
                res.user = u;
                return res;
            }

            @Override
            public ApiClient.Question[] generateQuiz(String level) { throw new UnsupportedOperationException(); }
        };

        RegisterController ctrl = new RegisterController(fake);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ApiClient.RegisterResponse.User> out = new AtomicReference<>();

        ctrl.register("Full", "user1", "e@e.com", "pass", new RegisterController.RegisterListener() {
            @Override public void onStart() { }
            @Override public void onComplete() { }
            @Override public void onSuccess(ApiClient.RegisterResponse.User user) { out.set(user); latch.countDown(); }
            @Override public void onError(String message) { latch.countDown(); }
        });

        boolean ok = latch.await(2, TimeUnit.SECONDS);
        assertTrue(ok);
        assertNotNull(out.get());
        assertEquals("user1", out.get().username);
    }

    @Test
    public void testRegisterError() throws Exception {
        ApiService fake = new ApiService() {
            @Override public ApiClient.LoginResponse login(String identifier, String password) { throw new UnsupportedOperationException(); }
            @Override public ApiClient.RegisterResponse register(String fullName, String username, String email, String password) {
                ApiClient.RegisterResponse res = new ApiClient.RegisterResponse();
                res.success = false;
                res.message = "exists";
                return res;
            }
            @Override public ApiClient.Question[] generateQuiz(String level) { throw new UnsupportedOperationException(); }
        };

        RegisterController ctrl = new RegisterController(fake);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> err = new AtomicReference<>();

        ctrl.register("Full", "user1", "e@e.com", "pass", new RegisterController.RegisterListener() {
            @Override public void onStart() { }
            @Override public void onComplete() { }
            @Override public void onSuccess(ApiClient.RegisterResponse.User user) { latch.countDown(); }
            @Override public void onError(String message) { err.set(message); latch.countDown(); }
        });

        boolean ok = latch.await(2, TimeUnit.SECONDS);
        assertTrue(ok);
        assertEquals("exists", err.get());
    }
}
