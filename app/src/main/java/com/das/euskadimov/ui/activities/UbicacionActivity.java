package com.das.euskadimov.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.das.euskadimov.R;
import com.das.euskadimov.databinding.ActivityUbicacionBinding;
import com.das.euskadimov.model.Centro;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import android.content.Intent;

public class UbicacionActivity extends AppCompatActivity {

    private LocationCallback locationCallback;

    enum LocationGrantedStatus {
        UNKONW,
        GRANTED,
        DENIED
    }

    ActivityUbicacionBinding binding;


    private static final int CODIGO_PERMISO_UBICACION = 100;
    private LocationGrantedStatus locationGrantedStatus;

    private MapView mapa;
    private Marker marcadorCentro;
    private Marker marcadorUsuario;

    private FusedLocationProviderClient proveedorLocalizacion;

    private Centro centro;
    private GeoPoint puntoCentro;
    private GeoPoint puntoUsuario;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );
        binding = ActivityUbicacionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        proveedorLocalizacion = LocationServices.getFusedLocationProviderClient(this);


        recogerDatosCentro();
        mapa = binding.mapaCentro;
        mostrarDatosCentro();
        configurarMapa();
        configurarBotones();
    }

    private void recogerDatosCentro() {
        centro = (Centro) getIntent().getSerializableExtra("CENTRO");
        puntoCentro = new GeoPoint(centro.getLatitud(), centro.getLongitud());
    }

    private void mostrarDatosCentro() {
        binding.tvNombreCentroUbicacion.setText(centro.getNombre());
        binding.tvUniversidadCentroUbicacion.setText(centro.getNombre() + " · " + centro.getCiudad());
        binding.tvDireccionCentroUbicacion.setText(centro.getDireccion());
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
        marcadorCentro.setTitle(centro.getNombre());
        marcadorCentro.setSubDescription(centro.getDireccion());

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
        binding.btnEnfocarDestino.setOnClickListener(v -> enfocarDestino());

        binding.btnEnfocarUsuario.setOnClickListener(v -> obtenerYEnfocarUbicacionActual());

        binding.btnUsarMiUbicacion.setOnClickListener(v -> abrirResultadosRuta("actual"));

        binding.btnUbicacionManual.setOnClickListener(v -> abrirResultadosRuta("manual"));
    }

    private void abrirResultadosRuta(String tipoOrigen) {
        Intent intent = new Intent(this, ResultadosRutaActivity.class);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        proveedorLocalizacion.getLastLocation().addOnSuccessListener(location -> {
            this.currentLocation = location;
            startActivity(intent);
        });

        intent.putExtra("CENTRO_NOMBRE", centro.getNombre());
        intent.putExtra("CENTRO_UNIVERSIDAD", centro.getUniversidad());
        intent.putExtra("CENTRO_CIUDAD", centro.getCiudad());
        intent.putExtra("CENTRO_DIRECCION", centro.getDireccion());
        intent.putExtra("CENTRO_LATITUD", centro.getLatitud());
        intent.putExtra("CENTRO_LONGITUD", centro.getLongitud());
        intent.putExtra("TIPO_ORIGEN", tipoOrigen);
        intent.putExtra("LOCATION", currentLocation);

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
        var locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000).build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                UbicacionActivity.this.currentLocation = locationResult.getLastLocation();
            }
        };

        proveedorLocalizacion.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
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
        if (locationCallback == null) {
            obtenerUltimaUbicacion();
        }


        if (mapa != null) {
            mapa.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        proveedorLocalizacion.removeLocationUpdates(locationCallback);
        if (mapa != null) {
            mapa.onPause();
        }
    }
}
