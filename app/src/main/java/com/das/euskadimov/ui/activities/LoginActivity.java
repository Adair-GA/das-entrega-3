package com.das.euskadimov.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.das.euskadimov.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGuest;
    private TextView tvGoToRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            abrirPantallaPrincipal();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGuest = findViewById(R.id.btnGuest);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        btnGuest.setOnClickListener(v -> entrarComoInvitado());

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioActual != null) {
            abrirPantallaPrincipal();
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Introduce tu correo");
            etEmail.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            etPassword.setError("Introduce tu contraseña");
            etPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                abrirPantallaPrincipal();
            } else {
                Toast.makeText(this, "Error al acceder. Revisa tus datos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void entrarComoInvitado() {
        mAuth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Has entrado como invitado", Toast.LENGTH_SHORT).show();
                abrirPantallaPrincipal();
            } else {
                Toast.makeText(this, "No se ha podido entrar como invitado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirPantallaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}