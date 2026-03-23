package com.strange.bootloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView logText;
    private TextView helloText;
    private EditText passwordInput;

    // Список систем для проверки
    private String[] systems = {"CPU", "GPU", "STORAGE", "PHONE"};
    private int currentStep = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Делаем Activity на весь экран (скрываем статус-бар)
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        
        setContentView(R.layout.activity_main);

        // Инициализация UI
        logText = findViewById(R.id.logText);
        helloText = findViewById(R.id.helloText);
        passwordInput = findViewById(R.id.passwordInput);

        // Настройка поля ввода пароля (слушаем нажатие Enter)
        passwordInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkPassword();
                return true;
            }
            return false;
        });

        // Запуск последовательности через 0.5 сек после открытия
        handler.postDelayed(this::startBootSequence, 500);
    }

    private void startBootSequence() {
        if (currentStep < systems.length) {
            // Пишем название системы (например, CPU)
            logText.append(systems[currentStep]);

            // Ждем 2 секунды перед появлением галочки ✓
            handler.postDelayed(() -> {
                logText.append(".........✓\n");
                currentStep++;

                // Ждем 0.2 секунды перед следующей строкой
                handler.postDelayed(this::startBootSequence, 200);
            }, 2000);

        } else {
            // Переходим к финальной стадии
            showHelloScreen();
        }
    }

    private void showHelloScreen() {
        // Плавно проявляем надпись (длительность 3 сек)
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(3000);
        helloText.startAnimation(fadeIn);
        helloText.setAlpha(1.0f);

        // Ждем 3 секунды, пока надпись висит, и решаем, что делать дальше
        handler.postDelayed(() -> {
            handleSecurityLogic();
        }, 3000);
    }

    private void handleSecurityLogic() {
        // Получаем сохраненный пароль (по умолчанию пустой)
        SharedPreferences prefs = getSharedPreferences("BootSettings", Context.MODE_PRIVATE);
        String savedPassword = prefs.getString("user_password", ""); // Пусто, если не настроено

        if (savedPassword.isEmpty()) {
            // ПАРОЛЯ НЕТ: Плавно исчезаем и закрываем приложение
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(1000);
            findViewById(android.R.id.content).startAnimation(fadeOut);
            
            handler.postDelayed(this::finish, 1000);
        } else {
            // ПАРОЛЬ ЕСТЬ: Показываем поле ввода
            passwordInput.setVisibility(View.VISIBLE);
            passwordInput.requestFocus();
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
