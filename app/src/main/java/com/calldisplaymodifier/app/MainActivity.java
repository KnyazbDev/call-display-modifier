package com.calldisplaymodifier.app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1002;
    private static final int ROLE_REQUEST_CODE = 1003;
    
    private TextView statusText;
    private Button permissionsButton;
    private Button enableServiceButton;
    private Button testOverlayButton;
    private CheckBox enableModificationCheckBox;
    private EditText prefixEditText;
    private EditText suffixEditText;
    
    // Remote control UI
    private CheckBox remoteModeCheckBox;
    private EditText serverUrlEditText;
    private Button registerButton;
    private Button syncButton;
    private Button testConnectionButton;
    private TextView connectionStatusText;
    
    private SharedPreferences preferences;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        apiClient = new ApiClient(this);
        
        // Показываем предупреждение при первом запуске
        if (!preferences.getBoolean("warning_accepted", false)) {
            showLegalWarning();
        }
        
        initializeViews();
        updateUI();
    }
    
    private void showLegalWarning() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.warning_legal)
            .setMessage(R.string.warning_text)
            .setCancelable(false)
            .setPositiveButton(R.string.btn_accept, (dialog, which) -> {
                preferences.edit().putBoolean("warning_accepted", true).apply();
            })
            .show();
    }
    
    private void initializeViews() {
        statusText = findViewById(R.id.statusText);
        permissionsButton = findViewById(R.id.permissionsButton);
        enableServiceButton = findViewById(R.id.enableServiceButton);
        testOverlayButton = findViewById(R.id.testOverlayButton);
        enableModificationCheckBox = findViewById(R.id.enableModificationCheckBox);
        prefixEditText = findViewById(R.id.prefixEditText);
        suffixEditText = findViewById(R.id.suffixEditText);
        
        // Remote control UI
        remoteModeCheckBox = findViewById(R.id.remoteModeCheckBox);
        serverUrlEditText = findViewById(R.id.serverUrlEditText);
        registerButton = findViewById(R.id.registerButton);
        syncButton = findViewById(R.id.syncButton);
        testConnectionButton = findViewById(R.id.testConnectionButton);
        connectionStatusText = findViewById(R.id.connectionStatusText);
        
        // Загрузка сохраненных настроек
        enableModificationCheckBox.setChecked(
            preferences.getBoolean("modification_enabled", true)
        );
        prefixEditText.setText(
            preferences.getString("prefix", "[Изменено] ")
        );
        suffixEditText.setText(
            preferences.getString("suffix", "")
        );
        
        // Загрузка настроек удаленного управления
        remoteModeCheckBox.setChecked(
            preferences.getBoolean("remote_mode_enabled", false)
        );
        serverUrlEditText.setText(
            preferences.getString("server_url", "http://192.168.1.100:3000")
        );
        
        // Обработчики кнопок
        permissionsButton.setOnClickListener(v -> requestAllPermissions());
        enableServiceButton.setOnClickListener(v -> enableInCallService());
        testOverlayButton.setOnClickListener(v -> testOverlay());
        
        // Remote control buttons
        registerButton.setOnClickListener(v -> registerWithServer());
        syncButton.setOnClickListener(v -> syncRules());
        testConnectionButton.setOnClickListener(v -> testConnection());
        
        // Сохранение настроек при изменении
        enableModificationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("modification_enabled", isChecked).apply();
        });
        
        prefixEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                preferences.edit().putString("prefix", prefixEditText.getText().toString()).apply();
            }
        });
        
        suffixEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                preferences.edit().putString("suffix", suffixEditText.getText().toString()).apply();
            }
        });
        
        // Remote control settings
        remoteModeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("remote_mode_enabled", isChecked).apply();
            updateRemoteControlUI();
        });
        
        serverUrlEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String url = serverUrlEditText.getText().toString();
                preferences.edit().putString("server_url", url).apply();
                apiClient.setServerUrl(url);
            }
        });
        
        updateRemoteControlUI();
    }
    
    private void updateRemoteControlUI() {
        boolean remoteEnabled = remoteModeCheckBox.isChecked();
        serverUrlEditText.setEnabled(remoteEnabled);
        registerButton.setEnabled(remoteEnabled);
        syncButton.setEnabled(remoteEnabled);
        testConnectionButton.setEnabled(remoteEnabled);
    }
    
    private void registerWithServer() {
        String url = serverUrlEditText.getText().toString();
        apiClient.setServerUrl(url);
        
        connectionStatusText.setText("🔄 Регистрация...");
        
        apiClient.registerClient(new ApiClient.ApiCallback<ApiClient.RegisterResponse>() {
            @Override
            public void onSuccess(ApiClient.RegisterResponse result) {
                runOnUiThread(() -> {
                    connectionStatusText.setText("✅ Устройство зарегистрировано: " + result.client_id.substring(0, 8) + "...");
                    Toast.makeText(MainActivity.this, "Успешно зарегистрировано!", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    connectionStatusText.setText("❌ Ошибка: " + error);
                    Toast.makeText(MainActivity.this, "Ошибка регистрации: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void syncRules() {
        connectionStatusText.setText("🔄 Синхронизация...");
        
        apiClient.getRules(new ApiClient.ApiCallback<ApiClient.RulesResponse>() {
            @Override
            public void onSuccess(ApiClient.RulesResponse result) {
                runOnUiThread(() -> {
                    int count = result.rules.size();
                    connectionStatusText.setText("✅ Загружено правил: " + count);
                    Toast.makeText(MainActivity.this, "Синхронизировано " + count + " правил", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    connectionStatusText.setText("❌ Ошибка: " + error);
                    Toast.makeText(MainActivity.this, "Ошибка синхронизации: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void testConnection() {
        String url = serverUrlEditText.getText().toString();
        apiClient.setServerUrl(url);
        
        connectionStatusText.setText("🔄 Проверка подключения...");
        
        apiClient.testConnection(new ApiClient.ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    connectionStatusText.setText("✅ Подключено к серверу");
                    Toast.makeText(MainActivity.this, "Соединение успешно!", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    connectionStatusText.setText("❌ Ошибка: " + error);
                    Toast.makeText(MainActivity.this, "Ошибка подключения: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void updateUI() {
        boolean hasPermissions = checkAllPermissions();
        boolean hasOverlayPermission = checkOverlayPermission();
        
        if (hasPermissions && hasOverlayPermission) {
            statusText.setText(R.string.status_ready);
            statusText.setTextColor(getColor(R.color.success_green));
            permissionsButton.setEnabled(false);
            enableServiceButton.setEnabled(true);
            testOverlayButton.setEnabled(true);
        } else {
            statusText.setText(R.string.status_permissions_needed);
            statusText.setTextColor(getColor(R.color.warning_red));
            permissionsButton.setEnabled(true);
            enableServiceButton.setEnabled(false);
            testOverlayButton.setEnabled(false);
        }
    }
    
    private boolean checkAllPermissions() {
        String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE
        };
        
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }
    
    private void requestAllPermissions() {
        // Запрос базовых разрешений
        String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE
        };
        
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        
        // Запрос разрешения на overlay
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
    
    private void enableInCallService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TelecomManager telecomManager = getSystemService(TelecomManager.class);
            if (telecomManager != null) {
                Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
                intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, 
                                getPackageName());
                startActivityForResult(intent, ROLE_REQUEST_CODE);
            }
        } else {
            Toast.makeText(this, "Требуется Android 6.0 или выше", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void testOverlay() {
        // Тестовый показ overlay
        Intent intent = new Intent(this, OverlayService.class);
        intent.setAction("TEST_OVERLAY");
        intent.putExtra("phone_number", "+1234567890");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        
        Toast.makeText(this, "Тестовое отображение overlay", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                Toast.makeText(this, "Разрешения предоставлены", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Некоторые разрешения не предоставлены", 
                              Toast.LENGTH_SHORT).show();
            }
            
            updateUI();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Разрешение на overlay предоставлено", 
                                  Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Разрешение на overlay не предоставлено", 
                                  Toast.LENGTH_SHORT).show();
                }
            }
            updateUI();
        } else if (requestCode == ROLE_REQUEST_CODE) {
            updateUI();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}

