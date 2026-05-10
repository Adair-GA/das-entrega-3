package com.das.euskadimov.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.das.euskadimov.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView tvWelcomeName;
    private TextView tvToolbarInitials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setupUI();
    }

    private void setupUI() {
        setContentView(R.layout.activity_main);

        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvToolbarInitials = findViewById(R.id.tvToolbarInitials);

        mostrarDatosUsuario();

        findViewById(R.id.cardDeusto).setOnClickListener(v -> openCentros("Deusto"));
        findViewById(R.id.cardEHU).setOnClickListener(v -> openCentros("EHU"));
        findViewById(R.id.cardMondragon).setOnClickListener(v -> openCentros("Mondragon"));

        android.widget.TextView tvInitials = findViewById(R.id.tvToolbarInitials);
        tvInitials.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            // Usuario anónimo no tiene email
            String inicial = (email != null && !email.isEmpty())
                    ? email.substring(0, 1).toUpperCase()
                    : "?";
            tvInitials.setText(inicial);
        }
    }

    private void mostrarDatosUsuario() {
        FirebaseUser usuario = mAuth.getCurrentUser();

        String nombreMostrar = "Usuario";

        if (usuario != null) {
            if (usuario.isAnonymous()) {
                nombreMostrar = "Invitado";
            } else if (usuario.getDisplayName() != null && !usuario.getDisplayName().trim().isEmpty()) {
                nombreMostrar = usuario.getDisplayName().trim();
            } else if (usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty()) {
                nombreMostrar = usuario.getEmail().split("@")[0];
            }
        }

        tvWelcomeName.setText("Hola, " + nombreMostrar + " 👋");
        tvToolbarInitials.setText(obtenerIniciales(nombreMostrar));
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "--";
        }

        String[] partes = nombre.trim().split("\\s+");

        if (partes.length == 1) {
            String palabra = partes[0];
            return palabra.substring(0, Math.min(2, palabra.length())).toUpperCase();
        }

        String primera = partes[0].substring(0, 1);
        String segunda = partes[1].substring(0, 1);

        return (primera + segunda).toUpperCase();
    }

    private void openCentros(String uniName) {
        Intent intent = new Intent(MainActivity.this, CentrosActivity.class);
        intent.putExtra("SELECTED_UNI", uniName);
        startActivity(intent);
    }
}