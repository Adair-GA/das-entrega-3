package com.das.euskadimov;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPass, etConfirmPass;
    private Button btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etPass = findViewById(R.id.etRegPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();
        String confirm = etConfirmPass.getText().toString();

        if (pass.equals(confirm) && pass.length() >= 6) {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Registro completado", Toast.LENGTH_SHORT).show();
                    finish(); // Esto cierra la actividad y vuelve al Login
                } else {
                    Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }
    }
}