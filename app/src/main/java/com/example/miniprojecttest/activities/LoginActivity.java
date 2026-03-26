package com.example.miniprojecttest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojecttest.CategoryActivity;
import com.example.miniprojecttest.ProductDetailActivity;
import com.example.miniprojecttest.R;
import com.example.miniprojecttest.dal.AppDatabase;
import com.example.miniprojecttest.dal.DatabaseSeeder;
import com.example.miniprojecttest.entities.User;
import com.example.miniprojecttest.session.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.card.MaterialCardView;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "productId";

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private AppDatabase db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Launcher screen should always ensure sample data exists.
        DatabaseSeeder.seed(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        db = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            navigateAfterLogin(sessionManager.getUserId(), sessionManager.getUsername());
            return;
        }

        // Entrance animations
        View heroBlock = findViewById(R.id.heroBlock);
        MaterialCardView formCard = findViewById(R.id.formCard);
        if (heroBlock != null) {
            heroBlock.setAlpha(0f);
            heroBlock.animate().alpha(1f).setDuration(500).start();
        }
        if (formCard != null) {
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
            slideUp.setStartOffset(200);
            formCard.startAnimation(slideUp);
        }

        Button btnLogin = findViewById(R.id.btnLogin);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                // Scale press feedback
                v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(80).withEndAction(this::login).start()
                ).start();
            });
        }
    }

    private void login() {
        String username = etUsername.getText() == null ? "" : etUsername.getText().toString().trim();
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = db.userDAO().login(username, password);
        if (user == null) {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionManager.saveLogin(user.getId(), user.getUsername());
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        navigateAfterLogin(user.getId(), user.getUsername());
    }

    private void navigateAfterLogin(int userId, String username) {
        int productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        Intent intent;

        if (productId != -1) {
            intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, productId);
        } else {
            intent = new Intent(this, CategoryActivity.class);
        }

        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}
