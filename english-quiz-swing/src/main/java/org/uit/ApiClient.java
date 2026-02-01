package org.uit;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:3000";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    private static final Gson gson = new Gson();

    public static class LoginResponse {
        public boolean success;
        public String message;
        public String token;
        public User user;

        public static class User {
            public String _id;
            public String fullName;
            public String username;
            public String email;
            public String recentScore;
        }
    }

    public static class RegisterResponse {
        public boolean success;
        public String message;
        public User user;

        public static class User {
            public String _id;
            public String fullName;
            public String username;
            public String email;
            public String recentScore;
            public String createdAt;
        }
    }

    public static RegisterResponse register(String fullName, String username, String email, String password) throws IOException {
        String json = gson.toJson(Map.of("fullName", fullName, "username", username, "email", email, "password", password));

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/auth/register")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, RegisterResponse.class);
        }
    }

    public static class Question {
        public String question;
        public Map<String, String> Choice;
        public String Correct;
    }

    public static Question[] generateQuiz(String level) throws IOException {
        String json = gson.toJson(Map.of("level", level));

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/quiz/generate-quiz")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, Question[].class);
        }
    }

    public static LoginResponse login(String identifier, String password) throws IOException {
        String json = gson.toJson(Map.of("identifier", identifier, "password", password));

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/auth/login")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, LoginResponse.class);
        }
    }

    public static boolean changePassword(String userId, String newPassword, String token) throws IOException {

        
        String json = gson.toJson(Map.of(
                "newPassword", newPassword
        ));

        RequestBody body = RequestBody.create(
                json,
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/auth/change-password")
                .put(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String err = response.body() != null ? response.body().string() : "no body";
                System.err.println("Change password failed: " + response.code());
                System.err.println("Response body: " + err);
                return false;
            }
            return true;
        }
    }

    public static class SubmitScoreResponse {
        public boolean success;
        public String recentScore;
    }

    public static SubmitScoreResponse submitScore(double scorePercent, String token) throws IOException {
        String json = gson.toJson(Map.of("score", scorePercent));

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/quiz/submit-score")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String err = response.body() != null ? response.body().string() : "no body";
                throw new IOException("Submit score failed: " + response.code() + " - " + err);
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, SubmitScoreResponse.class);
        }
    }
}