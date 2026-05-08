package com.das.euskadimov.ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import androidx.appcompat.app.AppCompatActivity;

import com.das.euskadimov.R;
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
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        String confirm = etConfirmPass.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Introduce tu nombre");
            etName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Introduce tu correo");
            etEmail.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            etPass.setError("Introduce una contraseña");
            etPass.requestFocus();
            return;
        }

        if (confirm.isEmpty()) {
            etConfirmPass.setError("Confirma la contraseña");
            etConfirmPass.requestFocus();
            return;
        }

        if (!pass.equals(confirm)) {
            etConfirmPass.setError("Las contraseñas no coinciden");
            etConfirmPass.requestFocus();
            return;
        }

        if (pass.length() < 6) {
            etPass.setError("La contraseña debe tener al menos 6 caracteres");
            etPass.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser usuario = mAuth.getCurrentUser();

                if (usuario != null) {
                    UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    usuario.updateProfile(perfil).addOnCompleteListener(profileTask -> {
                        if (profileTask.isSuccessful()) {
                            Toast.makeText(this, "Registro completado", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Usuario creado, pero no se pudo guardar el nombre", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(this, "Registro completado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                String mensaje = "Error al registrar usuario";

                if (task.getException() != null && task.getException().getMessage() != null) {
                    mensaje = task.getException().getMessage();
                }

                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}
