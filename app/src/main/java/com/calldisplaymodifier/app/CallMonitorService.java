package com.calldisplaymodifier.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * Сервис мониторинга входящих звонков
 * Использует InCallService API для отслеживания звонков
 */
public class CallMonitorService extends InCallService {
    
    private static final String TAG = "CallMonitorService";
    private static final String CHANNEL_ID = "CallMonitorChannel";
    private static final int NOTIFICATION_ID = 1001;
    
    private Call.Callback callCallback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            handleCallStateChange(call, state);
        }
    };
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "CallMonitorService создан");
        createNotificationChannel();
        startForegroundService();
    }
    
    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Log.d(TAG, "Добавлен звонок: " + call.getDetails().getHandle());
        
        call.registerCallback(callCallback);
        handleCallStateChange(call, call.getState());
    }
    
    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Log.d(TAG, "Звонок завершен");
        
        call.unregisterCallback(callCallback);
        
        // Останавливаем overlay сервис
        Intent intent = new Intent(this, OverlayService.class);
        intent.setAction("HIDE_OVERLAY");
        startService(intent);
    }
    
    private void handleCallStateChange(Call call, int state) {
        String phoneNumber = "Unknown";
        
        if (call.getDetails().getHandle() != null) {
            phoneNumber = call.getDetails().getHandle().getSchemeSpecificPart();
        }
        
        Log.d(TAG, "Состояние звонка изменилось: " + state + ", номер: " + phoneNumber);
        
        switch (state) {
            case Call.STATE_RINGING:
                // Входящий звонок
                Log.d(TAG, "Входящий звонок от: " + phoneNumber);
                showOverlay(phoneNumber);
                break;
                
            case Call.STATE_ACTIVE:
                // Звонок принят
                Log.d(TAG, "Звонок активен");
                hideOverlay();
                break;
                
            case Call.STATE_DISCONNECTED:
                // Звонок завершен
                Log.d(TAG, "Звонок завершен");
                hideOverlay();
                break;
        }
    }
    
    private void showOverlay(String phoneNumber) {
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        
        if (!preferences.getBoolean("modification_enabled", true)) {
            return;
        }
        
        Intent intent = new Intent(this, OverlayService.class);
        intent.setAction("SHOW_OVERLAY");
        intent.putExtra("phone_number", phoneNumber);
        intent.putExtra("prefix", preferences.getString("prefix", "[Изменено] "));
        intent.putExtra("suffix", preferences.getString("suffix", ""));
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }
    
    private void hideOverlay() {
        Intent intent = new Intent(this, OverlayService.class);
        intent.setAction("HIDE_OVERLAY");
        startService(intent);
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_desc);
            int importance = NotificationManager.IMPORTANCE_LOW;
            
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    private void startForegroundService() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(android.R.drawable.ic_menu_call)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, 
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "CallMonitorService уничтожен");
    }
}

