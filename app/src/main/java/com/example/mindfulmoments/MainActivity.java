package com.example.mindfulmoments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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