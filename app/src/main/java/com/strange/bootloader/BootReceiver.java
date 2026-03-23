package com.strange.bootloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Проверяем несколько типов сигналов загрузки для надежности
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || 
            "android.intent.action.QUICKBOOT_POWERON".equals(action)) {
            
            // Намерение запустить главный экран
            Intent i = new Intent(context, MainActivity.class);
            
            // FLAG_ACTIVITY_NEW_TASK обязателен для запуска из ресивера
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Чтобы не создавать копию экрана, если он уже открыт
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            
            context.startActivity(i);
        }
    }
}

