package com.das.euskadimov.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.das.euskadimov.Centro;
import com.das.euskadimov.R;
import com.das.euskadimov.ui.lists.adapters.CentroAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritosActivity extends AppCompatActivity {

    private RecyclerView rvFavoritos;
    private TextView tvSinFavoritos;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null || usuario.isAnonymous()) {
            Toast.makeText(this, "Inicia sesión para ver tus favoritos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        uid = usuario.getUid();
        db = FirebaseFirestore.getInstance();

        rvFavoritos = findViewById(R.id.rvFavoritos);
        tvSinFavoritos = findViewById(R.id.tvSinFavoritos);
        rvFavoritos.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnBackFavoritos).setOnClickListener(v -> finish());

        cargarFavoritos();
    }

    private void cargarFavoritos() {
        db.collection("usuarios").document(uid)
                .collection("favoritos")
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<Centro> favoritos = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : snapshots) {
                        try {
                            int id = ((Long) doc.get("id")).intValue();
                            String universidad = doc.getString("universidad");
                            String nombre = doc.getString("nombre");
                            String ciudad = doc.getString("ciudad");
                            String direccion = doc.getString("direccion");
                            double latitud = doc.getDouble("latitud");
                            double longitud = doc.getDouble("longitud");
                            favoritos.add(new Centro(id, universidad, nombre,
                                    ciudad, direccion, latitud, longitud));
                        } catch (Exception e) {
                            // Documento malformado, lo ignoramos
                        }
                    }

                    if (favoritos.isEmpty()) {
                        mostrarListaVacia();
                    } else {
                        mostrarFavoritos(favoritos);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar favoritos", Toast.LENGTH_SHORT).show());
    }

    private void mostrarFavoritos(List<Centro> favoritos) {
        tvSinFavoritos.setVisibility(View.GONE);
        rvFavoritos.setVisibility(View.VISIBLE);

        CentroAdapter adapter = new CentroAdapter(
                favoritos,
                uid,
                true, // modoFavoritos: al quitar estrella desaparece el ítem
                centro -> abrirUbicacion(centro),
                this::mostrarListaVacia  // callback cuando la lista queda vacía
        );
        rvFavoritos.setAdapter(adapter);
    }

    private void mostrarListaVacia() {
        tvSinFavoritos.setVisibility(View.VISIBLE);
        rvFavoritos.setVisibility(View.GONE);
    }

    private void abrirUbicacion(Centro centro) {
        Intent intent = new Intent(this, UbicacionActivity.class);
        intent.putExtra("CENTRO_ID", centro.getId());
        intent.putExtra("CENTRO_NOMBRE", centro.getNombre());
        intent.putExtra("CENTRO_UNIVERSIDAD", centro.getUniversidad());
        intent.putExtra("CENTRO_CIUDAD", centro.getCiudad());
        intent.putExtra("CENTRO_DIRECCION", centro.getDireccion());
        intent.putExtra("CENTRO_LATITUD", centro.getLatitud());
        intent.putExtra("CENTRO_LONGITUD", centro.getLongitud());
        startActivity(intent);
    }
}