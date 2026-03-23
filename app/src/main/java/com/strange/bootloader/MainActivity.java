package com.strange.bootloader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView logText, helloText;
    private EditText passwordInput;
    private View rootLayout;

    private String[] systems = {"CPU", "GPU", "STORAGE", "PHONE"};
    private int currentSystemIndex = 0;
    private int currentCharIndex = 0;
    private boolean isSceneSkipped = false;
    private long lastClickTime = 0;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Скрываем лишние элементы интерфейса
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        
        setContentView(R.layout.activity_main);

        // Проверка разрешения на отображение поверх окон (для автозапуска)
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }

        logText = findViewById(R.id.logText);
        helloText = findViewById(R.id.helloText);
        passwordInput = findViewById(R.id.passwordInput);
        rootLayout = findViewById(android.R.id.content);

        // Пропуск по нажатию
        rootLayout.setOnClickListener(v -> handleSkip());

        typeNextSystem();
    }

    private void typeNextSystem() {
        if (isSceneSkipped) return;

        if (currentSystemIndex < systems.length) {
            String currentStr = systems[currentSystemIndex];
            
            if (currentCharIndex < currentStr.length()) {
                logText.append(String.valueOf(currentStr.charAt(currentCharIndex)));
                currentCharIndex++;
                handler.postDelayed(this::typeNextSystem, 100); // 0.1 сек на букву
            } else {
                handler.postDelayed(() -> {
                    if (!isSceneSkipped) {
                        logText.append(".........✓\n");
                        currentSystemIndex++;
                        currentCharIndex = 0;
                        handler.postDelayed(this::typeNextSystem, 200);
                    }
                }, 1000);
            }
        } else {
            showHelloScreen();
        }
    }

    private void handleSkip() {
        if (isSceneSkipped) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 2000) {
            skipAnimation();
        } else {
            Toast.makeText(this, "Нажми ещё раз чтобы пропустить", Toast.LENGTH_SHORT).show();
        }
        lastClickTime = currentTime;
    }

    private void skipAnimation() {
        isSceneSkipped = true;
        handler.removeCallbacksAndMessages(null);
        logText.setText("");
        for (String s : systems) {
            logText.append(s + ".........✓\n");
        }
        showHelloScreen();
    }

    private void showHelloScreen() {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(2000);
        helloText.startAnimation(fadeIn);
        helloText.setAlpha(1.0f);
        
        // Здесь можно добавить логику появления поля пароля
    }

    @Override
    public void onBackPressed() {
        // Блокируем кнопку "Назад", чтобы нельзя было выйти из заставки
    }
}
