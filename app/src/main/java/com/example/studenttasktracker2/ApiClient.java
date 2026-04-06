package com.example.studenttasktracker2;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {

    private static final String BASE_URL = "https://student-task-tracker-ap7h.onrender.com";
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onSuccess(JSONObject response);
        void onError(String errorMessage);
    }

    public static void get(String path, Callback callback) {
        request("GET", path, null, callback);
    }

    public static void post(String path, JSONObject body, Callback callback) {
        request("POST", path, body, callback);
    }

    public static void patch(String path, JSONObject body, Callback callback) {
        request("PATCH", path, body, callback);
    }

    public static void delete(String path, Callback callback) {
        request("DELETE", path, null, callback);
    }

    private static void request(String method, String path, JSONObject body, Callback callback) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(BASE_URL + path);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                if (body != null) {
                    connection.setDoOutput(true);
                    byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(input);
                    }
                }

                int statusCode = connection.getResponseCode();
                InputStream stream = statusCode >= 200 && statusCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                String responseText = readStream(stream);
                JSONObject responseJson = parseJsonSafely(responseText);

                if (statusCode >= 200 && statusCode < 300) {
                    mainHandler.post(() -> callback.onSuccess(responseJson));
                } else {
                    String message = responseJson.optString("message", "Request failed with status " + statusCode);
                    mainHandler.post(() -> callback.onError(message));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private static String readStream(InputStream stream) throws Exception {
        if (stream == null) return "{}";
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString().isEmpty() ? "{}" : builder.toString();
    }

    private static JSONObject parseJsonSafely(String raw) {
        try {
            return new JSONObject(raw);
        } catch (JSONException e) {
            JSONObject fallback = new JSONObject();
            try {
                fallback.put("message", raw);
            } catch (JSONException ignored) {
            }
            return fallback;
        }
    }
}
