package com.das.euskadimov;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.das.euskadimov.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
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