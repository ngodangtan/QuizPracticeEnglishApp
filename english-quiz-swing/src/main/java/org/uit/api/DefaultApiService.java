package org.uit.api;

import org.uit.ApiClient;

public class DefaultApiService implements ApiService {
    @Override
    public ApiClient.LoginResponse login(String identifier, String password) throws Exception {
        return ApiClient.login(identifier, password);
    }

    @Override
    public ApiClient.RegisterResponse register(String fullName, String username, String email, String password) throws Exception {
        return ApiClient.register(fullName, username, email, password);
    }

    @Override
    public ApiClient.Question[] generateQuiz(String level) throws Exception {
        return ApiClient.generateQuiz(level);
    }
}
