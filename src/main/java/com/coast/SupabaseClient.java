package com.coast;

import okhttp3.*;
import com.google.gson.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SupabaseClient {

    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final String BASE_URL = "https://zw4lbcamf5.execute-api.us-east-1.amazonaws.com/prod";

    private SupabaseClient() {}

    private static Request.Builder baseRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json");
    }

    // ── Fetch ─────────────────────────────────────────────────────────────────

    public static List<String> fetchExistingUserIds() throws IOException {
        Request request = baseRequest(BASE_URL + "/fetch-existing-user-ids").get().build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body().string();
            if (!response.isSuccessful()) throw new IOException(body);

            JsonArray arr = JsonParser.parseString(body).getAsJsonArray();
            List<String> ids = new ArrayList<>();
            for (JsonElement el : arr)
                ids.add(el.getAsJsonObject().get("user_id").getAsString());
            return ids;
        }
    }

    public static User fetchUserByEmail(String email) throws IOException {
        String url = BASE_URL + "/fetch-user-by-email?email=" + email;
        Request request = baseRequest(url).get().build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body().string();
            if (!response.isSuccessful()) throw new IOException(body);

            JsonArray arr = JsonParser.parseString(body).getAsJsonArray();
            if (arr.size() == 0) return null;

            JsonObject o = arr.get(0).getAsJsonObject();
            return new User(
                    o.get("user_id").getAsString(),
                    o.get("name").getAsString(),
                    o.get("email").getAsString(),
                    o.get("password").getAsString(),
                    o.get("birth_date").getAsString(),
                    o.has("about") && !o.get("about").isJsonNull()
                            ? o.get("about").getAsString() : null,
                    o.has("created_at") && !o.get("created_at").isJsonNull()
                            ? o.get("created_at").getAsString() : null,
                    o.has("last_sign_in") && !o.get("last_sign_in").isJsonNull()
                            ? o.get("last_sign_in").getAsString() : null,
                    o.has("last_sign_out") && !o.get("last_sign_out").isJsonNull()
                            ? o.get("last_sign_out").getAsString() : null
            );
        }
    }

    public static boolean emailExists(String email) throws IOException {
        String url = BASE_URL + "/email-exists?email=" + email;
        Request request = baseRequest(url).get().build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body().string();
            if (!response.isSuccessful()) throw new IOException(body);
            return JsonParser.parseString(body).getAsJsonObject().get("exists").getAsBoolean();
        }
    }

    // ── Insert ────────────────────────────────────────────────────────────────

    public static void insertUser(User user) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("user_id",    user.getUserId());
        json.addProperty("name",       user.getName());
        json.addProperty("email",      user.getEmail());
        json.addProperty("password",   user.getPassword());
        json.addProperty("birth_date", user.getBirthDate());
        if (user.getAbout() != null)
            json.addProperty("about", user.getAbout());

        RequestBody body = RequestBody.create(gson.toJson(json), MediaType.parse("application/json"));
        Request request = baseRequest(BASE_URL + "/insert-user").post(body).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(response.body().string());
        }
    }

    // ── Update ────────────────────────────────────────────────────────────────

    private static void patch(String userId, JsonObject json) throws IOException {
        String url = BASE_URL + "/update-user?user_id=" + userId;
        RequestBody body = RequestBody.create(gson.toJson(json), MediaType.parse("application/json"));
        Request request = baseRequest(url).patch(body).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(response.body().string());
        }
    }

    public static void updateLastSignIn(String userId) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("last_sign_in", java.time.Instant.now().toString());
        patch(userId, json);
    }

    public static void updateLastSignOut(String userId) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("last_sign_out", java.time.Instant.now().toString());
        patch(userId, json);
    }

    public static void updateName(String userId, String name) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        patch(userId, json);
    }

    public static void updateEmail(String userId, String email) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        patch(userId, json);
    }

    public static void updatePassword(String userId, String password) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("password", password);
        patch(userId, json);
    }

    public static void updateAbout(String userId, String about) throws IOException {
        JsonObject json = new JsonObject();
        if (about != null) json.addProperty("about", about);
        else json.add("about", JsonNull.INSTANCE);
        patch(userId, json);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public static void deleteUser(String userId) throws IOException {
        String url = BASE_URL + "/delete-user?user_id=" + userId;
        Request request = baseRequest(url).delete().build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(response.body().string());
        }
    }
}
