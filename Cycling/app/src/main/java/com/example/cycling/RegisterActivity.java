package com.example.cycling;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameEt, emailEt, passwordEt;
    DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        usernameEt = findViewById(R.id.usernameEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);

        db = DBHelper.getInstance(this);

        findViewById(R.id.registerBtn).setOnClickListener(v -> register());
        findViewById(R.id.loginBtn).setOnClickListener(v -> openLogin());
        findViewById(R.id.guestBtn).setOnClickListener(view -> openMainAsGuest());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void register() {
        boolean success = db.registerUser(
                usernameEt.getText().toString(),
                emailEt.getText().toString(),
                passwordEt.getText().toString()
        );

        if (success) {
            Toast.makeText(this, "Registracija uspešna", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Korisnik već postoji", Toast.LENGTH_SHORT).show();
        }
    }

    private void openLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void openMainAsGuest() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}