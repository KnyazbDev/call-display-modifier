package com.calldisplaymodifier.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * API клиент для работы с удаленным сервером
 */
public class ApiClient {
    
    private static final String TAG = "ApiClient";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final Context context;
    private final OkHttpClient client;
    private final Gson gson;
    private final SharedPreferences prefs;
    
    // URL сервера - ВАЖНО: замените на ваш реальный URL!
    private String serverUrl;
    
    public ApiClient(Context context) {
        this.context = context;
        this.gson = new Gson();
        this.prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        
        // Загружаем URL сервера из настроек
        this.serverUrl = prefs.getString("server_url", "http://192.168.1.100:3000");
        
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Установить URL сервера
     */
    public void setServerUrl(String url) {
        this.serverUrl = url;
        prefs.edit().putString("server_url", url).apply();
    }
    
    /**
     * Получить URL сервера
     */
    public String getServerUrl() {
        return serverUrl;
    }
    
    /**
     * Регистрация клиента на сервере
     */
    public void registerClient(final ApiCallback<RegisterResponse> callback) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceName = android.os.Build.MODEL;
        
        String json = gson.toJson(new RegisterRequest(deviceId, deviceName));
        RequestBody body = RequestBody.create(json, JSON);
        
        Request request = new Request.Builder()
                .url(serverUrl + "/api/client/register")
                .post(body)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to register client", e);
                callback.onFailure("Connection error: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    RegisterResponse registerResponse = gson.fromJson(responseBody, RegisterResponse.class);
                    
                    // Сохраняем client_id
                    prefs.edit().putString("client_id", registerResponse.client_id).apply();
                    
                    Log.d(TAG, "Client registered: " + registerResponse.client_id);
                    callback.onSuccess(registerResponse);
                } else {
                    callback.onFailure("Server error: " + response.code());
                }
            }
        });
    }
    
    /**
     * Получить все правила для текущего клиента
     */
    public void getRules(final ApiCallback<RulesResponse> callback) {
        String clientId = prefs.getString("client_id", null);
        
        if (clientId == null) {
            callback.onFailure("Client not registered");
            return;
        }
        
        Request request = new Request.Builder()
                .url(serverUrl + "/api/client/" + clientId + "/rules")
                .get()
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch rules", e);
                callback.onFailure("Connection error: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    RulesResponse rulesResponse = gson.fromJson(responseBody, RulesResponse.class);
                    
                    Log.d(TAG, "Loaded " + rulesResponse.rules.size() + " rules");
                    callback.onSuccess(rulesResponse);
                } else {
                    callback.onFailure("Server error: " + response.code());
                }
            }
        });
    }
    
    /**
     * Получить правило для конкретного номера
     */
    public void getRuleForNumber(String phoneNumber, final ApiCallback<RuleCheckResponse> callback) {
        String clientId = prefs.getString("client_id", null);
        
        if (clientId == null) {
            callback.onFailure("Client not registered");
            return;
        }
        
        Request request = new Request.Builder()
                .url(serverUrl + "/api/client/" + clientId + "/rule/" + phoneNumber)
                .get()
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to check rule", e);
                callback.onFailure("Connection error: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    RuleCheckResponse ruleCheck = gson.fromJson(responseBody, RuleCheckResponse.class);
                    callback.onSuccess(ruleCheck);
                } else {
                    callback.onFailure("Server error: " + response.code());
                }
            }
        });
    }
    
    /**
     * Проверить подключение к серверу
     */
    public void testConnection(final ApiCallback<String> callback) {
        Request request = new Request.Builder()
                .url(serverUrl + "/api/health")
                .get()
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Connection failed: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess("Connected successfully");
                } else {
                    callback.onFailure("Server returned: " + response.code());
                }
            }
        });
    }
    
    // ==========================================
    // DATA CLASSES
    // ==========================================
    
    public static class RegisterRequest {
        String device_id;
        String device_name;
        
        public RegisterRequest(String device_id, String device_name) {
            this.device_id = device_id;
            this.device_name = device_name;
        }
    }
    
    public static class RegisterResponse {
        public String client_id;
        public String message;
    }
    
    public static class RulesResponse {
        public List<Rule> rules = new ArrayList<>();
    }
    
    public static class Rule {
        public int id;
        public String original_number;
        public String display_number;
        public String description;
    }
    
    public static class RuleCheckResponse {
        public boolean found;
        public String display_number;
        public String description;
    }
    
    // ==========================================
    // CALLBACK INTERFACE
    // ==========================================
    
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }
}

