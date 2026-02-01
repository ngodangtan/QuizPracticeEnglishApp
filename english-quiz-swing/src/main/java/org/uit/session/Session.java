package org.uit.session;

public class Session {
    private static String userId;
    private static String name;
    private static String email;
    private static String token;
    private static String recentScore;

    public static String getUserId() { return userId; }
    public static void setUserId(String userId) { Session.userId = userId; }

    public static String getName() { return name; }
    public static void setName(String name) { Session.name = name; }

    public static String getEmail() { return email; }
    public static void setEmail(String email) { Session.email = email; }

    public static String getToken() { return token; }
    public static void setToken(String token) { Session.token = token; }

    public static String getRecentScore() { return recentScore; }
    public static void setRecentScore(String recentScore) { Session.recentScore = recentScore; }
}
