package com.das.euskadimov.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.das.euskadimov.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import android.content.Intent;

public class UbicacionActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISO_UBICACION = 100;

    private MapView mapa;
    private Marker marcadorCentro;
    private Marker marcadorUsuario;

    private FusedLocationProviderClient proveedorLocalizacion;

    private String nombreCentro;
    private String universidadCentro;
    private String ciudadCentro;
    private String direccionCentro;
    private double latitudCentro;
    private double longitudCentro;

    private GeoPoint puntoCentro;
    private GeoPoint puntoUsuario;

    private TextView tvNombreCentro;
    private TextView tvUniversidadCentro;
    private TextView tvDireccionCentro;

    private Button btnUsarMiUbicacion;
    private Button btnUbicacionManual;
    private Button btnEnfocarDestino;
    private Button btnEnfocarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );

        setContentView(R.layout.activity_ubicacion);

        proveedorLocalizacion = LocationServices.getFusedLocationProviderClient(this);

        recogerDatosCentro();
        enlazarVistas();
        mostrarDatosCentro();
        configurarMapa();
        configurarBotones();
    }

    private void recogerDatosCentro() {
        nombreCentro = getIntent().getStringExtra("CENTRO_NOMBRE");
        universidadCentro = getIntent().getStringExtra("CENTRO_UNIVERSIDAD");
        ciudadCentro = getIntent().getStringExtra("CENTRO_CIUDAD");
        direccionCentro = getIntent().getStringExtra("CENTRO_DIRECCION");
        latitudCentro = getIntent().getDoubleExtra("CENTRO_LATITUD", 43.2630);
        longitudCentro = getIntent().getDoubleExtra("CENTRO_LONGITUD", -2.9350);

        if (nombreCentro == null) {
            nombreCentro = "Centro seleccionado";
        }

        if (universidadCentro == null) {
            universidadCentro = "Universidad no indicada";
        }

        if (ciudadCentro == null) {
            ciudadCentro = "";
        }

        if (direccionCentro == null) {
            direccionCentro = "";
        }

        puntoCentro = new GeoPoint(latitudCentro, longitudCentro);
    }

    private void enlazarVistas() {
        tvNombreCentro = findViewById(R.id.tvNombreCentroUbicacion);
        tvUniversidadCentro = findViewById(R.id.tvUniversidadCentroUbicacion);
        tvDireccionCentro = findViewById(R.id.tvDireccionCentroUbicacion);

        btnUsarMiUbicacion = findViewById(R.id.btnUsarMiUbicacion);
        btnUbicacionManual = findViewById(R.id.btnUbicacionManual);
        btnEnfocarDestino = findViewById(R.id.btnEnfocarDestino);
        btnEnfocarUsuario = findViewById(R.id.btnEnfocarUsuario);

        mapa = findViewById(R.id.mapaCentro);
    }

    private void mostrarDatosCentro() {
        tvNombreCentro.setText(nombreCentro);
        tvUniversidadCentro.setText(universidadCentro + " · " + ciudadCentro);
        tvDireccionCentro.setText(direccionCentro);
    }

    private void configurarMapa() {
        mapa.setTileSource(TileSourceFactory.MAPNIK);
        mapa.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapa.setMultiTouchControls(true);

        mapa.getController().setZoom(16.0);
        mapa.getController().setCenter(puntoCentro);

        marcadorCentro = new Marker(mapa);
        marcadorCentro.setPosition(puntoCentro);
        marcadorCentro.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marcadorCentro.setTitle(nombreCentro);
        marcadorCentro.setSubDescription(direccionCentro);

        marcadorCentro.setOnMarkerClickListener((marker, mapView) -> {
            Toast.makeText(
                    UbicacionActivity.this,
                    marker.getTitle(),
                    Toast.LENGTH_SHORT
            ).show();
            marker.showInfoWindow();
            return true;
        });

        mapa.getOverlays().add(marcadorCentro);
        mapa.invalidate();
    }

    private void configurarBotones() {
        btnEnfocarDestino.setOnClickListener(v -> enfocarDestino());

        btnEnfocarUsuario.setOnClickListener(v -> obtenerYEnfocarUbicacionActual());

        btnUsarMiUbicacion.setOnClickListener(v -> abrirResultadosRuta("actual"));

        btnUbicacionManual.setOnClickListener(v -> abrirResultadosRuta("manual"));
    }

    private void abrirResultadosRuta(String tipoOrigen) {
        Intent intent = new Intent(this, ResultadosRutaActivity.class);

        intent.putExtra("CENTRO_NOMBRE", nombreCentro);
        intent.putExtra("CENTRO_UNIVERSIDAD", universidadCentro);
        intent.putExtra("CENTRO_CIUDAD", ciudadCentro);
        intent.putExtra("CENTRO_DIRECCION", direccionCentro);
        intent.putExtra("CENTRO_LATITUD", latitudCentro);
        intent.putExtra("CENTRO_LONGITUD", longitudCentro);
        intent.putExtra("TIPO_ORIGEN", tipoOrigen);

        startActivity(intent);
    }

    private void enfocarDestino() {
        mapa.getController().animateTo(puntoCentro);
        mapa.getController().setZoom(16.0);
    }

    private void obtenerYEnfocarUbicacionActual() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            obtenerUltimaUbicacion();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PERMISO_UBICACION
            );
        }
    }

    private void obtenerUltimaUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        proveedorLocalizacion.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        actualizarMarcadorUsuario(location);
                    } else {
                        Toast.makeText(
                                this,
                                "No se ha podido obtener la ubicación actual. En el emulador puedes fijarla manualmente.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .addOnFailureListener(this, e ->
                        Toast.makeText(
                                this,
                                "Error al obtener la ubicación",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void actualizarMarcadorUsuario(Location location) {
        puntoUsuario = new GeoPoint(location.getLatitude(), location.getLongitude());

        if (marcadorUsuario == null) {
            marcadorUsuario = new Marker(mapa);
            marcadorUsuario.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            marcadorUsuario.setTitle("Mi ubicación");
            marcadorUsuario.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_usuario_rojo));
            mapa.getOverlays().add(marcadorUsuario);
        }

        marcadorUsuario.setPosition(puntoUsuario);

        mapa.getController().animateTo(puntoUsuario);
        mapa.getController().setZoom(17.0);
        mapa.invalidate();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CODIGO_PERMISO_UBICACION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUltimaUbicacion();
            } else {
                Toast.makeText(
                        this,
                        "Sin permiso de ubicación no se puede centrar el mapa en tu posición",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mapa != null) {
            mapa.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mapa != null) {
            mapa.onPause();
        }
    }
}