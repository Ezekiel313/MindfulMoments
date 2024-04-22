package com.example.mindfulmoments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.ui.AppBarConfiguration;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(Color.BLACK);
        ConstraintLayout main = findViewById(R.id.main);

        int color = ((ColorDrawable) main.getBackground()).getColor();
        toolbar.setBackgroundColor(color);// Set the title text color after setting support action bar

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        // Define top-level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.main // ID of the start destination
        ).build();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void MeditationButtonPressed(View view) {
        Intent intent = new Intent(MainActivity.this,Meditation.class);
        startActivity(intent);
    }

    public void AffirmationButtonPressed(View view) {
        Intent intent = new Intent(MainActivity.this,Affirmations.class);
        startActivity(intent);
    }

    public void SilentMeditationButtonPressed(View view) {
        Intent intent = new Intent(MainActivity.this,SilentMeditations.class);
        startActivity(intent);
    }

    public void SleepMeditationButtonPressed(View view) {
        Intent intent = new Intent(MainActivity.this,SleepMeditation .class);
        startActivity(intent);
    }
}
