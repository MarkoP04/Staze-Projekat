package com.example.cycling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEt, passwordEt;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        usernameEt = findViewById(R.id.usernameEt);
        passwordEt = findViewById(R.id.passwordEt);

        db = DBHelper.getInstance(this);

        findViewById(R.id.loginBtn).setOnClickListener(v -> login());
        findViewById(R.id.guestBtn).setOnClickListener(v -> openMainAsGuest());
        findViewById(R.id.registerBtn).setOnClickListener(v -> openRegister());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void login() {
        int userId = db.loginUser(
                usernameEt.getText().toString(),
                passwordEt.getText().toString()
        );

        if (userId != -1) {
            saveUserSession(userId);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Pogrešan email ili lozinka", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMainAsGuest() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void openRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void saveUserSession(int userId) {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        prefs.edit().putInt("user_id", userId).apply();
    }
}