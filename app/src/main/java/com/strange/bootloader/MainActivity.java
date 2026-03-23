package com.strange.bootloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
        
        // Полный экран без лишних элементов
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        
        setContentView(R.layout.activity_main);

        logText = findViewById(R.id.logText);
        helloText = findViewById(R.id.helloText);
        passwordInput = findViewById(R.id.passwordInput);
        rootLayout = findViewById(android.R.id.content);

        // Обработка нажатия для пропуска
        rootLayout.setOnClickListener(v -> handleSkip());

        // Начинаем печатать первую систему
        typeNextSystem();
    }

    private void typeNextSystem() {
        if (isSceneSkipped) return;

        if (currentSystemIndex < systems.length) {
            String currentStr = systems[currentSystemIndex];
            
            if (currentCharIndex < currentStr.length()) {
                // Печатаем по одной букве каждые 0.1 сек
                logText.append(String.valueOf(currentStr.charAt(currentCharIndex)));
                currentCharIndex++;
                handler.postDelayed(this::typeNextSystem, 100); 
            } else {
                // Слово напечатано, ждем 2 сек перед галочкой
                handler.postDelayed(() -> {
                    if (!isSceneSkipped) {
                        logText.append(".........✓\n");
                        currentSystemIndex++;
                        currentCharIndex = 0;
                        // Пауза 0.2 сек перед следующим словом
                        handler.postDelayed(this::typeNextSystem, 200);
                    }
                }, 2000);
            }
        } else {
            showHelloScreen();
        }
    }

    private void handleSkip() {
        if (isSceneSkipped) return;

        long currentTime = System.currentTimeMillis();
        // Если нажали еще раз в течение 2 секунд
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
        for (String s : systems) logText.append(s + ".........✓\n");
        showHelloScreen();
    }

    private void showHelloScreen() {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(2000);
        helloText.startAnimation(fadeIn);
        helloText.setAlpha(1.0f);

        handler.postDelayed(this::finishScene, 3000);
    }

    private void finishScene() {
        // Логика закрытия или пароля
        finish();
    }

    @Override
    public void onBackPressed() { /* Заблокировано */ }
}
            // Можно вызвать клавиатуру автоматически, если нужно
        }
    }

    private void checkPassword() {
        SharedPreferences prefs = getSharedPreferences("BootSettings", Context.MODE_PRIVATE);
        String correctPass = prefs.getString("user_password", "");
        String enteredPass = passwordInput.getText().toString();

        if (enteredPass.equals(correctPass)) {
            // Пароль верный — закрываем экран разблокировки
            finish();
        } else {
            // Ошибка пароля (можно добавить красное мигание)
            passwordInput.setText("");
            Toast.makeText(this, "ACCESS DENIED", Toast.LENGTH_SHORT).show();
        }
    }

    // Блокируем кнопку "Назад", чтобы нельзя было просто закрыть экран загрузки
    @Override
    public void onBackPressed() {
        // Ничего не делаем
    }
}
