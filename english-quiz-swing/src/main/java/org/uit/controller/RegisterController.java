package org.uit.controller;

import org.uit.ApiClient;
import org.uit.api.ApiService;
import org.uit.api.DefaultApiService;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class RegisterController {

    public interface RegisterListener {
        void onStart();
        void onComplete();
        void onSuccess(ApiClient.RegisterResponse.User user);
        void onError(String message);
    }

    private final ApiService apiService;

    public RegisterController() { this(new DefaultApiService()); }

    public RegisterController(ApiService apiService) { this.apiService = apiService; }

    public void register(String fullName, String username, String email, String password, RegisterListener listener) {
        listener.onStart();

        new SwingWorker<ApiClient.RegisterResponse, Void>() {
            @Override
            protected ApiClient.RegisterResponse doInBackground() throws Exception {
                return apiService.register(fullName, username, email, password);
            }

            @Override
            protected void done() {
                listener.onComplete();
                try {
                    ApiClient.RegisterResponse res = get();
                    if (res.success) {
                        listener.onSuccess(res.user);
                    } else {
                        listener.onError(res.message);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    listener.onError("Registration failed: " + ex.getCause().getMessage());
                }
            }
        }.execute();
    }
}
