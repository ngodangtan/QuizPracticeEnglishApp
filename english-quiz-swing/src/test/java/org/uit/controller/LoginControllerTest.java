package org.uit.controller;

import org.junit.jupiter.api.Test;
import org.uit.ApiClient;
import org.uit.api.ApiService;
import org.uit.session.Session;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class LoginControllerTest {

    @Test
    public void testLoginSuccess() throws Exception {
        // Arrange
        ApiService fake = new ApiService() {
            @Override
            public ApiClient.LoginResponse login(String identifier, String password) {
                ApiClient.LoginResponse res = new ApiClient.LoginResponse();
                res.success = true;
                res.message = "ok";
                res.token = "tok";
                ApiClient.LoginResponse.User u = new ApiClient.LoginResponse.User();
                u._id = "u123";
                u.username = "tester";
                u.email = "t@t.com";
                res.user = u;
                return res;
            }

            @Override
            public ApiClient.RegisterResponse register(String fullName, String username, String email, String password) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ApiClient.Question[] generateQuiz(String level) {
                throw new UnsupportedOperationException();
            }
        };

        LoginController ctrl = new LoginController(fake);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ApiClient.LoginResponse> out = new AtomicReference<>();

        // Reset session
        Session.setUserId(null);
        Session.setName(null);
        Session.setEmail(null);
        Session.setToken(null);

        // Act
        ctrl.login("a@b.c", "pass", new LoginController.LoginListener() {
            @Override
            public void onStart() { }

            @Override
            public void onComplete() { }

            @Override
            public void onSuccess(ApiClient.LoginResponse res) {
                out.set(res);
                latch.countDown();
            }

            @Override
            public void onError(String message) {
                latch.countDown();
            }
        });

        boolean ok = latch.await(2, TimeUnit.SECONDS);
        assertTrue(ok, "Callback did not occur");

        ApiClient.LoginResponse r = out.get();
        assertNotNull(r);
        assertTrue(r.success);
        assertEquals("tester", Session.getName());
        assertEquals("u123", Session.getUserId());
        assertEquals("tok", Session.getToken());
    }

    @Test
    public void testLoginError() throws Exception {
        ApiService fake = new ApiService() {
            @Override
            public ApiClient.LoginResponse login(String identifier, String password) {
                ApiClient.LoginResponse res = new ApiClient.LoginResponse();
                res.success = false;
                res.message = "invalid";
                return res;
            }

            @Override
            public ApiClient.RegisterResponse register(String fullName, String username, String email, String password) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ApiClient.Question[] generateQuiz(String level) {
                throw new UnsupportedOperationException();
            }
        };

        LoginController ctrl = new LoginController(fake);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> err = new AtomicReference<>();

        ctrl.login("a@b.c", "pass", new LoginController.LoginListener() {
            @Override public void onStart() { }
            @Override public void onComplete() { }
            @Override public void onSuccess(ApiClient.LoginResponse res) { latch.countDown(); }
            @Override public void onError(String message) { err.set(message); latch.countDown(); }
        });

        boolean ok = latch.await(2, TimeUnit.SECONDS);
        assertTrue(ok);
        assertEquals("invalid", err.get());
    }
}
