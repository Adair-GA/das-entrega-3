package com.das.euskadimov.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.das.euskadimov.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPass, etConfirmPass;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        etName        = findViewById(R.id.etRegName);
        etEmail       = findViewById(R.id.etRegEmail);
        etPass        = findViewById(R.id.etRegPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnRegister   = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String name    = etName.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();
        String pass    = etPass.getText().toString().trim();
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

        btnRegister.setEnabled(false);
        Toast.makeText(this, "Registrando cuenta...", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        btnRegister.setEnabled(true);
                        String mensaje = "Error al registrar usuario";
                        if (task.getException() != null && task.getException().getMessage() != null) {
                            mensaje = task.getException().getMessage();
                        }
                        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                        return;
                    }

                    FirebaseUser usuario = mAuth.getCurrentUser();
                    if (usuario == null) {
                        abrirPantallaPrincipal();
                        return;
                    }

                    UserProfileChangeRequest perfilRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    usuario.updateProfile(perfilRequest)
                            .addOnCompleteListener(this, profileTask ->
                                    guardarEnFirestoreYNavegar(usuario.getUid(), name, email)
                            );
                });
    }

    private void guardarEnFirestoreYNavegar(String uid, String nombre, String email) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("email", email);

        db.collection("usuarios")
                .document(uid)
                .set(datos)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Error al guardar el perfil", Toast.LENGTH_SHORT).show();
                    }
                    abrirPantallaPrincipal();
                });
    }

    private void abrirPantallaPrincipal() {
        Toast.makeText(this, "¡Cuenta creada con éxito! 🎉", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}