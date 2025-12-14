package org.uit;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:3000";
    private static final OkHttpClient client = new OkHttpClient();
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
}