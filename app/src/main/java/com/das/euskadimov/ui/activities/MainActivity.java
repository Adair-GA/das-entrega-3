package com.das.euskadimov.ui.activities;

import android.os.Bundle;

import com.das.euskadimov.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            launchSignInActivity();
            return;
        }

        setupUI();
    }

    private void setupUI() {
        setContentView(R.layout.activity_main);
        findViewById(R.id.cardDeusto).setOnClickListener(v -> openCentros("Deusto"));
        findViewById(R.id.cardEHU).setOnClickListener(v -> openCentros("EHU"));
        findViewById(R.id.cardMondragon).setOnClickListener(v -> openCentros("Mondragon"));
    }

    private void openCentros(String uniName) {
        Intent intent = new Intent(MainActivity.this, CentrosActivity.class);
        intent.putExtra("SELECTED_UNI", uniName);
        startActivity(intent);
    }


    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private void launchSignInActivity() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build()
        );

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            setupUI();
        } else {
            if (response == null) {
                Toast.makeText(this, "Si no deseas iniciar sesión, presiona 'Continuar como invitado'", Toast.LENGTH_SHORT).show();
                launchSignInActivity();
                return;
            }

            Log.e("Login", "onSignInResult: ", response.getError());
            finish();
        }
    }
}
