package com.example.mindfulmoments;

import java.util.HashMap;
import java.util.Timer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Meditation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private boolean isMeditationPlaying = false;
    private Button playButton;
    private MediaPlayer mediaPlayer;
    Timer timer = new Timer();

    String[] audioOptions = new String[] {"Deep meditation", "Thunderstorm", "Relaxing birds and piano", "Spring breeze of meditation", "Silence"};


// Assuming you have corresponding audio files named deep_meditation.mp3, thunderstorm.mp3, etc. in the raw folder




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meditation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner dropdown = findViewById(R.id.timeSelector);
        dropdown.setOnItemSelectedListener(this);
        //create a list of items for the spinner.
        String[] times = new String[]{"5 min", "10 min", "15 min", "20 min", "25 min","30 min", "45min", "1 hr"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, times);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

    }

    private void toggleMeditationState() {
        if (isMeditationPlaying) {
            pauseMeditation();
        } else {
            startMeditation();
        }
    }

    private void pauseMeditation() {
        // Change text to "Play"
        isMeditationPlaying = false;
        updateButtonText();
        // Perform other actions to pause the meditation session
    }

    private void startMeditation() {
        // Change text to "Pause"
        isMeditationPlaying = true;
        updateButtonText();
        MediaPlayer music = MediaPlayer.create(Meditation.this, R.raw.deep_meditation);
        music.start();

        // Perform other actions to start the meditation session
    }

    public void updateButtonText() {
        if (isMeditationPlaying) {

            playButton.setText(R.string.pause);
        } else {

            playButton.setText(R.string.play);
        }
    }
    public void PlaybuttonPressed(View view) {
        isMeditationPlaying = !isMeditationPlaying;
        toggleMeditationState();
        updateButtonText();
    }
    private void playAudio(int audioResourceId) {
        // Release previous MediaPlayer instance if it exists
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        // Create new MediaPlayer instance and start playing the audio file
        mediaPlayer = MediaPlayer.create(this, audioResourceId);
        mediaPlayer.start();
    }



    public void onNothingSelected(AdapterView<?> parent) {
        // Handle case where no item is selected (optional)
        // set silent as default for error handling purposes as well

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Handle item selection
        String selectedItem = parent.getItemAtPosition(position).toString();
        System.out.println(selectedItem);

    }

    private void convertToAudio(String selectedItem) {

    }
    private void convertToTime(String selectedItem) {
        if(selectedItem == String.valueOf('x')) {

        }
    }






}





