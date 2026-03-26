package com.example.miniprojecttest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojecttest.MainActivity;
import com.example.miniprojecttest.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                // Currently navigates to MainActivity as a placeholder
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            });
        }
    }
}
