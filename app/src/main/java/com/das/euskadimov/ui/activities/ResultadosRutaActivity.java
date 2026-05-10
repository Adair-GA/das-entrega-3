package com.das.euskadimov.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.das.euskadimov.R;
import com.das.euskadimov.RutaResultado;
import com.das.euskadimov.TramoRuta;
import com.das.euskadimov.ui.lists.adapters.RutaResultadoAdapter;

import java.util.ArrayList;
import java.util.List;

public class ResultadosRutaActivity extends AppCompatActivity {

    private RecyclerView rvRutas;
    private RutaResultadoAdapter adapter;

    private String nombreCentro;
    private String universidadCentro;
    private String ciudadCentro;
    private String direccionCentro;
    private String tipoOrigen;

    private int centroId;
    private double latitudCentro;
    private double longitudCentro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_ruta);

        recogerDatosIntent();
        configurarCabecera();
        configurarLista();
    }

    private void recogerDatosIntent() {
        centroId = getIntent().getIntExtra("CENTRO_ID", -1);
        nombreCentro = getIntent().getStringExtra("CENTRO_NOMBRE");
        universidadCentro = getIntent().getStringExtra("CENTRO_UNIVERSIDAD");
        ciudadCentro = getIntent().getStringExtra("CENTRO_CIUDAD");
        direccionCentro = getIntent().getStringExtra("CENTRO_DIRECCION");
        latitudCentro = getIntent().getDoubleExtra("CENTRO_LATITUD", 43.2630);
        longitudCentro = getIntent().getDoubleExtra("CENTRO_LONGITUD", -2.9350);
        tipoOrigen = getIntent().getStringExtra("TIPO_ORIGEN");

        if (nombreCentro == null) {
            nombreCentro = "Centro seleccionado";
        }

        if (universidadCentro == null) {
            universidadCentro = "";
        }

        if (ciudadCentro == null) {
            ciudadCentro = "";
        }

        if (direccionCentro == null) {
            direccionCentro = "";
        }

        if (tipoOrigen == null) {
            tipoOrigen = "actual";
        }
    }

    private void configurarCabecera() {
        TextView tvTitulo = findViewById(R.id.tvTituloResultados);
        TextView tvSubtitulo = findViewById(R.id.tvSubtituloResultados);
        TextView tvInfoOrigen = findViewById(R.id.tvInfoOrigen);

        tvTitulo.setText("Rutas disponibles");
        tvSubtitulo.setText(nombreCentro);

        if (tipoOrigen.equals("manual")) {
            tvInfoOrigen.setText("Origen: ubicación indicada manualmente");
        } else {
            tvInfoOrigen.setText("Origen: ubicación actual");
        }

        findViewById(R.id.btnBackResultados).setOnClickListener(v -> finish());
    }

    private void configurarLista() {
        rvRutas = findViewById(R.id.rvRutas);
        rvRutas.setLayoutManager(new LinearLayoutManager(this));

        List<RutaResultado> rutas = crearRutasDeEjemplo(); //Aqui enganchar el servicio

        adapter = new RutaResultadoAdapter(rutas, ruta -> abrirRutaEnMapa(ruta));
        rvRutas.setAdapter(adapter);
    }


    private List<RutaResultado> crearRutasDeEjemplo() {
        List<RutaResultado> rutas = new ArrayList<>();

        List<TramoRuta> tramos1 = new ArrayList<>();
        tramos1.add(new TramoRuta("21:18", "21:25", "A pie", "Caminar hasta Moyua", "520 m", "7 min", "€0"));
        tramos1.add(new TramoRuta("21:25", "21:35", "Metro", "Moyua → Deusto", "2,1 km", "10 min", "€1,70"));
        tramos1.add(new TramoRuta("21:35", "21:38", "A pie", "Caminar hasta el centro", "240 m", "3 min", "€0"));
        rutas.add(new RutaResultado(1, "21:18", "21:38", "🚶 🚇 🚶", "€170", tramos1));

        List<TramoRuta> tramos2 = new ArrayList<>();
        tramos2.add(new TramoRuta("21:19", "21:23", "A pie", "Moyua", "148 m", "4 min 13 s", "€365"));
        tramos2.add(new TramoRuta("21:23", "21:26", "Metro", "Línea De Metro Bilbao · Moyua → Santimami/San Mamés", "1,3 km", "2 min 24 s", "€744"));
        tramos2.add(new TramoRuta("21:26", "21:31", "A pie", "Caminar hasta destino", "284 m", "5 min 38 s", "€552"));
        rutas.add(new RutaResultado(2, "21:19", "21:31", "🚶 🚇 🚶", "€1661", tramos2));

        List<TramoRuta> tramos3 = new ArrayList<>();
        tramos3.add(new TramoRuta("21:22", "21:27", "A pie", "Caminar hasta parada cercana", "430 m", "5 min", "€0"));
        tramos3.add(new TramoRuta("21:27", "21:32", "Autobús", "Bilbobus hacia Deusto", "1,8 km", "5 min", "€1,35"));
        tramos3.add(new TramoRuta("21:32", "21:35", "A pie", "Caminar hasta el edificio", "210 m", "3 min", "€0"));
        rutas.add(new RutaResultado(3, "21:22", "21:35", "🚶 🚌 🚶", "€135", tramos3));

        List<TramoRuta> tramos4 = new ArrayList<>();
        tramos4.add(new TramoRuta("21:24", "21:31", "A pie", "Caminar hasta Abando", "650 m", "7 min", "€0"));
        tramos4.add(new TramoRuta("21:31", "21:34", "Tranvía", "Abando → Guggenheim", "1,1 km", "3 min", "€1,50"));
        tramos4.add(new TramoRuta("21:34", "21:36", "A pie", "Último tramo hasta destino", "160 m", "2 min", "€0"));
        rutas.add(new RutaResultado(4, "21:24", "21:36", "🚶 🚋 🚶", "€150", tramos4));

        List<TramoRuta> tramos5 = new ArrayList<>();
        tramos5.add(new TramoRuta("21:29", "21:34", "A pie", "Caminar hasta parada", "390 m", "5 min", "€0"));
        tramos5.add(new TramoRuta("21:34", "21:38", "Autobús", "Línea urbana", "1,5 km", "4 min", "€1,35"));
        tramos5.add(new TramoRuta("21:38", "21:41", "A pie", "Caminar hasta destino", "220 m", "3 min", "€0"));
        rutas.add(new RutaResultado(5, "21:29", "21:41", "🚶 🚌 🚶", "€135", tramos5));

        if (!rutas.isEmpty()) {
            rutas.get(1).setDesplegada(true);
        }

        return rutas;
    }

    private void abrirRutaEnMapa(RutaResultado ruta) {
        Intent intent = new Intent(this, UbicacionActivity.class);

        intent.putExtra("CENTRO_ID", centroId);
        intent.putExtra("CENTRO_NOMBRE", nombreCentro);
        intent.putExtra("CENTRO_UNIVERSIDAD", universidadCentro);
        intent.putExtra("CENTRO_CIUDAD", ciudadCentro);
        intent.putExtra("CENTRO_DIRECCION", direccionCentro);
        intent.putExtra("CENTRO_LATITUD", latitudCentro);
        intent.putExtra("CENTRO_LONGITUD", longitudCentro);


        intent.putExtra("MOSTRAR_RUTA", true);
        intent.putExtra("RUTA_ID", ruta.getId());

        startActivity(intent);
    }
}