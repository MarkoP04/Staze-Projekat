package com.example.cycling;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.cycling.ui.ProfileFragment;
import com.example.cycling.ui.TrailsFragment;
import com.example.cycling.ui.UploadFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    int userId;
    boolean isGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        isGuest = userId == -1;

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }
            else if (item.getItemId() == R.id.nav_trails) {
                selectedFragment = new TrailsFragment();
            }
            else if (item.getItemId() == R.id.nav_upload) {
                if (isGuest) {
                    Toast.makeText(this, "Morate se registrovati", Toast.LENGTH_SHORT).show();
                    return false;
                }
                selectedFragment = new UploadFragment();
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return true;
        });

        bottomNav.setSelectedItemId(R.id.nav_trails);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}