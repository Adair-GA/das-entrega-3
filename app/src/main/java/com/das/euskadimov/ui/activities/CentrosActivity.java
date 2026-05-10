package com.das.euskadimov.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.das.euskadimov.Centro;
import com.das.euskadimov.R;
import com.das.euskadimov.data.local.CentrosDbHelper;
import com.das.euskadimov.ui.lists.adapters.CentroAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CentrosActivity extends AppCompatActivity {

    private RecyclerView rvCentros;
    private CentroAdapter adapter;
    private List<Centro> listaCentros = new ArrayList<>();
    private CentrosDbHelper centrosDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centros);

        String uniSeleccionada = getIntent().getStringExtra("SELECTED_UNI");

        centrosDbHelper = new CentrosDbHelper(this);

        rvCentros = findViewById(R.id.rvCentros);
        rvCentros.setLayoutManager(new LinearLayoutManager(this));

        listaCentros = centrosDbHelper.obtenerCentrosPorUniversidad(uniSeleccionada);

        // Obtenemos el uid del usuario actual (null si es anónimo o no hay sesión)
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (usuario != null && !usuario.isAnonymous()) {
            uid = usuario.getUid();
        }

        adapter = new CentroAdapter(listaCentros, uid, centro -> {
            Intent intent = new Intent(CentrosActivity.this, UbicacionActivity.class);
            intent.putExtra("CENTRO_ID", centro.getId());
            intent.putExtra("CENTRO_NOMBRE", centro.getNombre());
            intent.putExtra("CENTRO_UNIVERSIDAD", centro.getUniversidad());
            intent.putExtra("CENTRO_CIUDAD", centro.getCiudad());
            intent.putExtra("CENTRO_DIRECCION", centro.getDireccion());
            intent.putExtra("CENTRO_LATITUD", centro.getLatitud());
            intent.putExtra("CENTRO_LONGITUD", centro.getLongitud());
            startActivity(intent);
        });

        rvCentros.setAdapter(adapter);
    }
}