
package com.das.euskadimov;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
public class CentrosActivity extends AppCompatActivity {

    private RecyclerView rvCentros;
    private CentroAdapter adapter;
    private List<Centro> listaFiltrada = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centros);

        // 1. Obtener la universidad seleccionada
        String uniSeleccionada = getIntent().getStringExtra("SELECTED_UNI");

        rvCentros = findViewById(R.id.rvCentros);
        rvCentros.setLayoutManager(new LinearLayoutManager(this));

        // 2. Obtener los datos desde la "Base de Datos"
        // Este método es el que conectarás con tu clase de SQLite
        listaFiltrada = obtenerCentrosDesdeDB(uniSeleccionada);

        // 3. Configurar el Adapter
        adapter = new CentroAdapter(listaFiltrada, centro -> {
            Intent intent = new Intent(CentrosActivity.this, UbicacionActivity.class);
            intent.putExtra("CENTRO_NOMBRE", centro.getNombre());
            startActivity(intent);
        });

        rvCentros.setAdapter(adapter);
    }

    /**
     * Este es el método que consultará tu SQLite.
     * De momento devuelve datos fijos, pero su estructura ya es la correcta.
     */
    private List<Centro> obtenerCentrosDesdeDB(String universidad) {
        List<Centro> resultados = new ArrayList<>();
        
        // AQUÍ IRÍA TU CONSULTA SQL: 
        // SELECT * FROM centros WHERE universidad = 'Deusto'
        
        if (universidad.equals("Deusto")) {
            resultados.add(new Centro("Facultad de Derecho", "Bilbao"));
            resultados.add(new Centro("Ingeniería (ESIDE)", "Bilbao"));
            resultados.add(new Centro("Campus San Sebastián", "Donostia"));
        } else if (universidad.equals("EHU")) {
            resultados.add(new Centro("Facultad de Bellas Artes", "Leioa"));
            resultados.add(new Centro("Facultad de Educación", "Vitoria-Gasteiz"));
            resultados.add(new Centro("Escuela de Ingeniería", "Bilbao"));
        } else if (universidad.equals("Mondragon")) {
            resultados.add(new Centro("Escuela Politécnica Superior", "Arrasate"));
            resultados.add(new Centro("Enpresagintza", "Oñati"));
        }

        return resultados;
    }
}