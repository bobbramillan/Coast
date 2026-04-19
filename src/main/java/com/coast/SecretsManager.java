package com.coast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class SecretsManager {

    private static final String SECRET_NAME = "coast/supabase";
    private static final Region REGION = Region.US_EAST_1;

    private static String supabaseUrl;
    private static String supabaseApiKey;

    static {
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(REGION)
                .build();

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(SECRET_NAME)
                .build();

        GetSecretValueResponse response = client.getSecretValue(request);
        JsonObject secret = JsonParser.parseString(response.secretString()).getAsJsonObject();

        supabaseUrl    = secret.get("SUPABASE_URL").getAsString();
        supabaseApiKey = secret.get("SUPABASE_API_KEY").getAsString();

        client.close();
    }

    private SecretsManager() {}

    public static String getSupabaseUrl()    { return supabaseUrl; }
    public static String getSupabaseApiKey() { return supabaseApiKey; }
}
