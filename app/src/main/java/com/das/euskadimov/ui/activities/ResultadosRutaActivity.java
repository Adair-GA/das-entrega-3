package com.das.euskadimov.ui.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.das.euskadimov.R;
import com.das.euskadimov.RutaResultado;
import com.das.euskadimov.TramoRuta;
import com.das.euskadimov.data.local.TripPatternHelper;
import com.das.euskadimov.data.remote.OtpClient;
import com.das.euskadimov.ui.lists.adapters.RutaResultadoAdapter;
import com.graphql.WalkingTripQuery;
import com.graphql.type.Leg;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

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

    private List<RutaResultado> processedRoutes;
    private List<WalkingTripQuery.TripPattern> tripPatterns;

    private Location originLocation;

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

        originLocation = getIntent().getParcelableExtra("LOCATION");
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
        var x = OtpClient.getInstance().queryWalking(originLocation.getLatitude(), originLocation.getLongitude(), latitudCentro, longitudCentro).subscribe(dataApolloResponse -> {
            var patterns = dataApolloResponse.dataOrThrow().getTrip().getTripPatterns();
            int i = 0;
            List<RutaResultado> rutas = new ArrayList<>();
            for (WalkingTripQuery.TripPattern pattern : patterns) {
                rutas.add(parseRoute(pattern, i));
                i++;
            }
            tripPatterns = patterns;
            this.processedRoutes = rutas;
            runOnUiThread(() -> {
                adapter.setRutas(rutas);
                adapter.notifyDataSetChanged();
            });

        });

        adapter = new RutaResultadoAdapter(new ArrayList<>(), ruta -> abrirRutaEnMapa(ruta));
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
        rutas.add(new RutaResultado(3, "21:22", "21:35", "  🚶", "€135", tramos3));

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


    private TramoRuta parseLeg(WalkingTripQuery.Leg leg) {
        String expectedStartDateTime = leg.getExpectedStartTime().toString();
        String expectedStartTime = expectedStartDateTime.substring(11, 16);
        String expectedEndDateTime = leg.getExpectedStartTime().toString();
        String expectedEndTime = expectedEndDateTime.substring(11, 16);

        String modeFriendly;

        switch (leg.getMode()) {
            case foot:
                modeFriendly = "A pie";
                break;
            case bus:
                modeFriendly = "En " + leg.getLine().getAuthority().getName();
                break;
            case metro:
                modeFriendly = "En metro";
                break;
            default:
                throw new RuntimeException("Not supported mode: " + leg.getMode().name());
        }

        String descripcion;

        switch (leg.getMode()) {
            case foot:
                descripcion = "Caminar desde " + leg.getFromPlace().getName() + " hasta " + leg.getToPlace().getName();
                break;
            case metro:
                descripcion = leg.getFromPlace().getName() + "→" + leg.getToPlace().getName();
                break;
            case bus:
                descripcion = String.format("%s (%s) \n %s → %s", leg.getLine().getName(), leg.getLine().getPublicCode(), leg.getFromPlace().getName(), leg.getToPlace().getName());
                break;
            case bicycle:
                descripcion = "Ir en bici desde " + leg.getFromPlace().getName() + " hasta " + leg.getToPlace().getName();
                break;
            default:
                throw new RuntimeException("Not supported mode: " + leg.getMode().name());
        }


        int duration = (Integer) leg.getDuration();

        var minutes = duration / 60;
        var seconds = duration % 60;
        String durationFriendly = String.format("%dm %ds", minutes, seconds);


        return new TramoRuta(expectedStartTime, expectedEndTime, modeFriendly, descripcion, leg.getDistance() + "m", durationFriendly, "0");
    }

    private RutaResultado parseRoute(WalkingTripQuery.TripPattern tripPattern, int id) {
        List<TramoRuta> legs = new ArrayList<>();
        StringBuilder resumen = new StringBuilder();


        for (WalkingTripQuery.Leg leg : tripPattern.getLegs()) {
            TramoRuta parsedLeg = parseLeg(leg);
            legs.add(parsedLeg);

            switch (leg.getMode()) {
                case foot:
                    resumen.append("🚶");
                    break;
                case metro:
                    resumen.append("🚇");
                    break;
                case bus:
                    resumen.append("🚌");
                    break;
                case bicycle:
                    resumen.append("🚲");
                    break;

            }

        }

        String expectedStartDateTime = tripPattern.getExpectedStartTime().toString();
        String expectedStartTime = expectedStartDateTime.substring(11, 16);
        String expectedEndDateTime = tripPattern.getExpectedStartTime().toString();
        String expectedEndTime = expectedEndDateTime.substring(11, 16);


        return new RutaResultado(id, expectedStartTime, expectedEndTime, resumen.toString(), "0", legs);
    }


    private void abrirRutaEnMapa(RutaResultado ruta) {
        GeoPoint start = new GeoPoint(originLocation.getLatitude(), originLocation.getLongitude());
        GeoPoint end = new GeoPoint(latitudCentro, longitudCentro);

        int index = processedRoutes.indexOf(ruta);

        WalkingTripQuery.TripPattern pattern = tripPatterns.get(index);


        Intent intent = new Intent(this, ShowRouteActivity.class);
        intent.putExtra("START", (Parcelable) start);
        intent.putExtra("END", (Parcelable) end);
        TripPatternHelper.getInstance().setLastPattern(pattern);
        startActivity(intent);

    }
}
