package com.coast;

import okhttp3.*;
import com.google.gson.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SupabaseClient {

    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final String SUPABASE_URL;
    private static final String SUPABASE_API_KEY;

    static {
        try (InputStream input = SupabaseClient.class
                .getClassLoader().getResourceAsStream("config.properties")) {
            Properties props = new Properties();
            props.load(input);
            SUPABASE_URL     = props.getProperty("SUPABASE_URL");
            SUPABASE_API_KEY = props.getProperty("SUPABASE_API_KEY");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private SupabaseClient() {}

    private static Request.Builder baseRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json");
    }

    // ── Fetch ─────────────────────────────────────────────────────────────────

    public static List<String> fetchExistingUserIds() throws IOException {
        Request request = baseRequest(SUPABASE_URL + "/rest/v1/users?select=user_id")
                .get().build();

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
        String url = SUPABASE_URL + "/rest/v1/users?email=eq." + email + "&select=*";
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
        String url = SUPABASE_URL + "/rest/v1/users?email=eq." + email + "&select=user_id";
        Request request = baseRequest(url).get().build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body().string();
            if (!response.isSuccessful()) throw new IOException(body);
            return JsonParser.parseString(body).getAsJsonArray().size() > 0;
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
        Request request = baseRequest(SUPABASE_URL + "/rest/v1/users")
                .addHeader("Prefer", "return=minimal")
                .post(body).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(response.body().string());
        }
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public static void updateLastSignIn(String userId) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("last_sign_in", java.time.Instant.now().toString());

        RequestBody body = RequestBody.create(gson.toJson(json), MediaType.parse("application/json"));
        Request request = baseRequest(SUPABASE_URL + "/rest/v1/users?user_id=eq." + userId)
                .addHeader("Prefer", "return=minimal")
                .patch(body).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(response.body().string());
        }
    }

    public static void updateLastSignOut(String userId) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("last_sign_out", java.time.Instant.now().toString());

        RequestBody body = RequestBody.create(gson.toJson(json), MediaType.parse("application/json"));
        Request request = baseRequest(SUPABASE_URL + "/rest/v1/users?user_id=eq." + userId)
                .addHeader("Prefer", "return=minimal")
                .patch(body).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(response.body().string());
        }
    }

    public static void updateName(String userId, String name) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);

        RequestBody body = RequestBody.create(gson.toJson(json), MediaType.parse("application/json"));
        Request request = baseRequest(SUPABASE_URL + "/rest/v1/users?user_id=eq." + userId)
                .addHeader("Prefer", "return=minimal")
                .patch(body).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(response.body().string());
        }
    }

    public static void updateEmail(String userId, String email) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("email", email);

        RequestBody body = RequestBody.create(gson.toJson(json), MediaType.parse("application/json"));
        Request request = baseRequest(SUPABASE_URL + "/rest/v1/users?user_id=eq." + userId)
                .addHeader("Prefer", "return=minimal")
                .patch(body).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(response.body().string());
        }
    }

    public static void updatePassword(String userId, String password) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("password", password);

        RequestBody body = RequestBody.create(gson.toJson(json), MediaType.parse("application/json"));
        Request request = baseRequest(SUPABASE_URL + "/rest/v1/users?user_id=eq." + userId)
                .addHeader("Prefer", "return=minimal")
                .patch(body).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(response.body().string());
        }
    }

    public static void updateAbout(String userId, String about) throws IOException {
        JsonObject json = new JsonObject();
        if (about != null) json.addProperty("about", about);
        else json.add("about", JsonNull.INSTANCE);

        RequestBody body = RequestBody.create(gson.toJson(json), MediaType.parse("application/json"));
        Request request = baseRequest(SUPABASE_URL + "/rest/v1/users?user_id=eq." + userId)
                .addHeader("Prefer", "return=minimal")
                .patch(body).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(response.body().string());
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public static void deleteUser(String userId) throws IOException {
        Request request = baseRequest(SUPABASE_URL + "/rest/v1/users?user_id=eq." + userId)
                .delete().build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(response.body().string());
        }
    }
}
