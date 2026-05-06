package com.das.euskadimov.ui.activities;

import android.os.Bundle;

import com.das.euskadimov.R;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 4. Configurar los clics
        setupCardClicks();
    }

    private void setupCardClicks() {
        findViewById(R.id.cardDeusto).setOnClickListener(v -> openCentros("Deusto"));
        findViewById(R.id.cardEHU).setOnClickListener(v -> openCentros("EHU"));
        findViewById(R.id.cardMondragon).setOnClickListener(v -> openCentros("Mondragon"));
    }

    private void openCentros(String uniName) {
        Intent intent = new Intent(MainActivity.this, CentrosActivity.class);
        intent.putExtra("SELECTED_UNI", uniName);
        startActivity(intent);
    }
}
