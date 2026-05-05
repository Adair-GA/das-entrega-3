package com.das.euskadimov;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class UbicacionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        // Opcional: Recuperar el nombre del centro para ponerlo en el título
        String nombreCentro = getIntent().getStringExtra("CENTRO_NOMBRE");
        if (getSupportActionBar() != null && nombreCentro != null) {
            getSupportActionBar().setTitle(nombreCentro);
            // Habilitar botón de volver atrás
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Para que el botón de "atrás" de la Toolbar funcione
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}