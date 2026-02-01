package org.uit.controller;

import org.uit.ApiClient;
import org.uit.api.ApiService;
import org.uit.api.DefaultApiService;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class LoginController {

    public interface LoginListener {
        void onStart();
        void onComplete();
        void onSuccess(ApiClient.LoginResponse res);
        void onError(String message);
    }

    private final ApiService apiService;

    public LoginController() { this(new DefaultApiService()); }

    public LoginController(ApiService apiService) { this.apiService = apiService; }

    public void login(String email, String password, LoginListener listener) {
        listener.onStart();

        new SwingWorker<ApiClient.LoginResponse, Void>() {
            @Override
            protected ApiClient.LoginResponse doInBackground() throws Exception {
                return apiService.login(email, password);
            }

            @Override
            protected void done() {
                listener.onComplete();
                try {
                    ApiClient.LoginResponse res = get();
                    if (res.success) {
                        // update session model
                        org.uit.session.Session.setUserId(res.user._id);
                        org.uit.session.Session.setName(res.user.username);
                        org.uit.session.Session.setEmail(res.user.email);
                        org.uit.session.Session.setToken(res.token);

                        listener.onSuccess(res);
                    } else {
                        listener.onError(res.message);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    listener.onError("Login failed: " + ex.getCause().getMessage());
                }
            }
        }.execute();
    }
}
