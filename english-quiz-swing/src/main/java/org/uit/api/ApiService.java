package org.uit.api;

import org.uit.ApiClient;

public interface ApiService {
    ApiClient.LoginResponse login(String identifier, String password) throws Exception;
    ApiClient.RegisterResponse register(String fullName, String username, String email, String password) throws Exception;
    ApiClient.Question[] generateQuiz(String level) throws Exception;
    ApiClient.SubmitScoreResponse submitScore(double scorePercent, String token) throws Exception;
}
