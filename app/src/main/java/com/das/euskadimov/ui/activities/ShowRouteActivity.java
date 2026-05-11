package com.das.euskadimov.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.das.euskadimov.R;
import com.das.euskadimov.data.local.TripPatternHelper;
import com.das.euskadimov.databinding.ActivityShowRouteBinding;
import com.graphql.WalkingTripQuery;
import com.graphql.type.Mode;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class ShowRouteActivity extends AppCompatActivity {
    private ActivityShowRouteBinding binding;

    private GeoPoint start;
    private GeoPoint end;

    private WalkingTripQuery.TripPattern tripPattern;

    private MapView mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityShowRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mapa = binding.mapaCentro;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        start = intent.getParcelableExtra("START");
        end = intent.getParcelableExtra("END");
        tripPattern = TripPatternHelper.getInstance().getLastPattern();

        setupMap();
        drawRoute();
    }

    private void setupMap() {
        mapa.setTileSource(TileSourceFactory.MAPNIK);
        mapa.setMultiTouchControls(true);
        mapa.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapa.getController().setZoom(15.0);
    }


    private void drawRoute() {
        for (WalkingTripQuery.Leg leg : tripPattern.getLegs()) {
            if (leg.getPointsOnLink() == null) continue;

            List<GeoPoint> puntosRuta = decodePolyline(leg.getPointsOnLink().getPoints());
            String colorString;
            int color;

            try {
                colorString = "#" + leg.getLine().getPresentation().getColour();
                color = Color.parseColor(colorString);
            } catch (NullPointerException | NumberFormatException e) {
                if (leg.getMode() == Mode.bus && leg.getLine().getAuthority().getName().equals("Bilbobus")) {
                    color = Color.parseColor("#fe0000");
                } else {
                    color = Color.BLACK;
                }
            }


            Polyline line = new Polyline(mapa);
            line.setColor(color);
            line.setOnClickListener((polyline, mapView, eventPos) -> false);
            line.setPoints(puntosRuta);

            line.getOutlinePaint().setStrokeWidth(10f);
            mapa.getOverlays().add(line);

            mapa.getController().setCenter(puntosRuta.get(0));
        }
        mapa.invalidate();
    }


    // Generado por Gemini el 11/05/2026 a las 21:28
    private List<GeoPoint> decodePolyline(String encoded) {
        List<GeoPoint> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            poly.add(new GeoPoint((double) lat / 1E5, (double) lng / 1E5));
        }
        return poly;
    }

}
