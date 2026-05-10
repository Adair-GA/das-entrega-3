package com.das.euskadimov.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.das.euskadimov.databinding.ActivityPerfilBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private ActivityPerfilBinding binding;

    private FirebaseAuth      auth;
    private FirebaseFirestore db;
    private FirebaseUser      currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth        = FirebaseAuth.getInstance();
        db          = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        cargarDatosPerfil();
        setupListeners();
    }

    private void cargarDatosPerfil() {
        if (currentUser == null) return;

        String email = currentUser.getEmail();
        binding.tvEmail.setText(email != null ? email : "Invitado");

        if (currentUser.isAnonymous()) return;

        DocumentReference ref = db.collection("usuarios").document(currentUser.getUid());
        ref.get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;

                    String nombre = snapshot.getString("nombre");

                    if (nombre != null) {
                        binding.tvName.setText(nombre);
                        binding.etNombre.setText(nombre);
                        binding.etNombre.setSelection(nombre.length());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar el perfil", Toast.LENGTH_SHORT).show()
                );
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnGuardar.setOnClickListener(v -> guardarCambios());
        binding.rowCambiarPassword.setOnClickListener(v -> enviarEmailResetPassword());
        binding.rowCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());
    }

    private void guardarCambios() {
        if (currentUser == null) return;

        String nombre = binding.etNombre.getText() != null
                ? binding.etNombre.getText().toString().trim() : "";

        if (nombre.isEmpty()) {
            binding.tilNombre.setError("El nombre no puede estar vacío");
            return;
        }
        binding.tilNombre.setError(null);
        guardarEnFirestore(nombre);
    }

    private void guardarEnFirestore(String nombre) {
        binding.btnGuardar.setEnabled(false);

        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombre);

        db.collection("usuarios")
                .document(currentUser.getUid())
                .set(datos, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    binding.tvName.setText(nombre);
                    binding.btnGuardar.setEnabled(true);
                    Toast.makeText(this, "Perfil actualizado ✓", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    binding.btnGuardar.setEnabled(true);
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void enviarEmailResetPassword() {
        if (currentUser == null || currentUser.getEmail() == null) return;

        auth.sendPasswordResetEmail(currentUser.getEmail())
                .addOnSuccessListener(unused ->
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Correo enviado")
                                .setMessage("Hemos enviado un enlace a "
                                        + currentUser.getEmail()
                                        + " para cambiar tu contraseña.")
                                .setPositiveButton("OK", null)
                                .show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void confirmarCerrarSesion() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres cerrar sesión?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    auth.signOut();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }
}