package com.calldisplaymodifier.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * Сервис для отображения overlay (наложения) поверх других приложений
 * Показывает измененную информацию о входящем звонке
 */
public class OverlayService extends Service {
    
    private static final String TAG = "OverlayService";
    private static final String CHANNEL_ID = "OverlayServiceChannel";
    private static final int NOTIFICATION_ID = 1002;
    
    private WindowManager windowManager;
    private View overlayView;
    private boolean isOverlayShowing = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OverlayService создан");
        
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createNotificationChannel();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }
        
        String action = intent.getAction();
        Log.d(TAG, "Действие: " + action);
        
        if ("SHOW_OVERLAY".equals(action) || "TEST_OVERLAY".equals(action)) {
            String phoneNumber = intent.getStringExtra("phone_number");
            String prefix = intent.getStringExtra("prefix");
            String suffix = intent.getStringExtra("suffix");
            
            showOverlay(phoneNumber, prefix, suffix);
            
            // Для тестового режима автоматически скрываем через 5 секунд
            if ("TEST_OVERLAY".equals(action)) {
                new Handler().postDelayed(() -> hideOverlay(), 5000);
            }
        } else if ("HIDE_OVERLAY".equals(action)) {
            hideOverlay();
        }
        
        return START_NOT_STICKY;
    }
    
    private void showOverlay(String phoneNumber, String prefix, String suffix) {
        if (isOverlayShowing) {
            hideOverlay();
        }
        
        // Запуск как foreground service
        startForegroundService();
        
        try {
            // Создаем view для overlay
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            overlayView = inflater.inflate(R.layout.overlay_call_info, null);
            
            // Настраиваем параметры окна
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                            : WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT
            );
            
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            params.y = 100; // Отступ от верха экрана
            
            // Заполняем данные
            TextView modifiedNumberText = overlayView.findViewById(R.id.modifiedNumberText);
            TextView originalNumberText = overlayView.findViewById(R.id.originalNumberText);
            
            if (prefix == null) prefix = "[Изменено] ";
            if (suffix == null) suffix = "";
            
            String modifiedNumber = prefix + phoneNumber + suffix;
            
            modifiedNumberText.setText(modifiedNumber);
            originalNumberText.setText("Оригинал: " + phoneNumber);
            
            // Добавляем view в window manager
            windowManager.addView(overlayView, params);
            isOverlayShowing = true;
            
            Log.d(TAG, "Overlay отображен для номера: " + phoneNumber);
            
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при отображении overlay", e);
        }
    }
    
    private void hideOverlay() {
        if (isOverlayShowing && overlayView != null) {
            try {
                windowManager.removeView(overlayView);
                overlayView = null;
                isOverlayShowing = false;
                Log.d(TAG, "Overlay скрыт");
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при скрытии overlay", e);
            }
        }
        
        stopForeground(true);
        stopSelf();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Overlay Service";
            String description = "Служба отображения информации о звонках";
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
                .setContentTitle("Отображение информации о звонке")
                .setContentText("Overlay активен")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        
        startForeground(NOTIFICATION_ID, notification);
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        hideOverlay();
        Log.d(TAG, "OverlayService уничтожен");
    }
}

