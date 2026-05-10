package com.das.euskadimov.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.das.euskadimov.R;
import com.das.euskadimov.databinding.ActivityPerfilBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    // ── Binding ───────────────────────────────────────────────────────────────
    private ActivityPerfilBinding binding;

    // ── Firebase ──────────────────────────────────────────────────────────────
    private FirebaseAuth      auth;
    private FirebaseFirestore db;
    private FirebaseStorage   storage;
    private FirebaseUser      currentUser;

    // ── Foto nueva seleccionada (null si no cambió) ───────────────────────────
    private Uri selectedImageUri = null;

    // ── Galería ───────────────────────────────────────────────────────────────
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK
                        && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this)
                         .load(selectedImageUri)
                         .circleCrop()
                         .into(binding.ivAvatar);
                }
            }
        );

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth        = FirebaseAuth.getInstance();
        db          = FirebaseFirestore.getInstance();
        storage     = FirebaseStorage.getInstance();
        currentUser = auth.getCurrentUser();

        cargarDatosPerfil();
        setupListeners();
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    private void cargarDatosPerfil() {
        if (currentUser == null) return;

        String email = currentUser.getEmail();
        binding.tvEmail.setText(email != null ? email : "Invitado");

        // Si es anónimo no tiene datos en Firestore, salimos sin error
        if (currentUser.isAnonymous()) return;

        DocumentReference ref = db.collection("usuarios").document(currentUser.getUid());
        ref.get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;

                    String nombre  = snapshot.getString("nombre");
                    String fotoUrl = snapshot.getString("fotoUrl");

                    if (nombre != null) {
                        binding.tvName.setText(nombre);
                        binding.etNombre.setText(nombre);
                    }

                    if (fotoUrl != null && !fotoUrl.isEmpty()) {
                        Glide.with(this)
                                .load(fotoUrl)
                                .circleCrop()
                                .placeholder(android.R.drawable.ic_menu_myplaces)
                                .into(binding.ivAvatar);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar el perfil", Toast.LENGTH_SHORT).show()
                );
    }

    private void setupListeners() {
        // Volver atrás
        binding.btnBack.setOnClickListener(v -> finish());

        // Cambiar foto
        binding.fabChangePhoto.setOnClickListener(v -> abrirGaleria());
        binding.ivAvatar.setOnClickListener(v -> abrirGaleria());

        // Guardar
        binding.btnGuardar.setOnClickListener(v -> guardarCambios());

        // Cambiar contraseña
        binding.rowCambiarPassword.setOnClickListener(v -> enviarEmailResetPassword());

        // Cerrar sesión
        binding.rowCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());
    }

    // ── Acciones ──────────────────────────────────────────────────────────────

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
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

        // ← FALTA ESTO:
        if (selectedImageUri != null) {
            subirFotoYGuardar(nombre);
        } else {
            guardarEnFirestore(nombre, null);
        }
    }

    private void subirFotoYGuardar(String nombre) {
        StorageReference ref = storage.getReference()
            .child("fotos_perfil/" + currentUser.getUid() + ".jpg");

        binding.btnGuardar.setEnabled(false);
        binding.btnGuardar.setText("Subiendo foto…");

        ref.putFile(selectedImageUri)
           .continueWithTask(task -> {
               if (!task.isSuccessful() && task.getException() != null) {
                   throw task.getException();
               }
               return ref.getDownloadUrl();
           })
           .addOnSuccessListener(uri ->
               guardarEnFirestore(nombre, uri.toString())
           )
           .addOnFailureListener(e -> {
               binding.btnGuardar.setEnabled(true);
               binding.btnGuardar.setText("Guardar cambios");
               Toast.makeText(this,
                   "Error al subir la foto: " + e.getMessage(),
                   Toast.LENGTH_SHORT).show();
           });
    }

    private void guardarEnFirestore(String nombre, @Nullable String fotoUrl) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre",       nombre);
        if (fotoUrl != null) datos.put("fotoUrl", fotoUrl);

        db.collection("usuarios")
          .document(currentUser.getUid())
          .set(datos, SetOptions.merge())
          .addOnSuccessListener(unused -> {
              binding.tvName.setText(nombre);
              binding.btnGuardar.setEnabled(true);
              binding.btnGuardar.setText("Guardar cambios");
              selectedImageUri = null;
              Toast.makeText(this, "Perfil actualizado ✓", Toast.LENGTH_SHORT).show();
          })
          .addOnFailureListener(e -> {
              binding.btnGuardar.setEnabled(true);
              binding.btnGuardar.setText("Guardar cambios");
              Toast.makeText(this,
                  "Error al guardar: " + e.getMessage(),
                  Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show()
            );
    }

    private void confirmarCerrarSesion() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Seguro que quieres cerrar sesión?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                auth.signOut();
                // Vuelve a MainActivity, que detectará usuario null y lanzará el login
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            })
            .show();
    }
}