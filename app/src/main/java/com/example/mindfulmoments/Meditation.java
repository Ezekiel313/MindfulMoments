package com.example.mindfulmoments;


import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Meditation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private boolean isMeditationPlaying = false;
    private Button playButton;
    private MediaPlayer mediaPlayer;
    Timer timer = new Timer();
    private ProgressBar progressBar;

    // set default timer to 10 min in milliseconds
    int default_countdown = 600000;
    private int elapsedTime = 0;
    int countdown;
    int audio;
    int audioDuration;
    private boolean isPaused = false;

    String[] audioOptions = new String[] {"Deep meditation", "Thunderstorm", "Relaxing birds and piano", "Spring breeze of meditation", "Silence"};


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

        Spinner timeSelector = findViewById(R.id.timeSelector);
        timeSelector.setOnItemSelectedListener(this);
        //create a list of items for the spinner.
        String[] times = new String[]{"5 min", "10 min", "15 min", "20 min", "25 min","30 min", "45min", "1 hr"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, times);
//set the spinners adapter to the previously created one.
        timeSelector.setAdapter(timeAdapter);

        Spinner audioSelector = findViewById(R.id.selectAudio);
        audioSelector.setOnItemSelectedListener(this);
        //create a list of items for the spinner.
        String[] audioOptions = new String[] {"Deep meditation", "Thunderstorm", "Relaxing birds and piano", "Spring breeze of meditation", "Silence"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> audioAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, audioOptions);
//set the spinners adapter to the previously created one.
        audioSelector.setAdapter(audioAdapter);

        progressBar = findViewById(R.id.ProgressBar);
        playButton = findViewById(R.id.playButton);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    private void toggleMeditationState() {
        if (isMeditationPlaying) {
            pauseMeditation();
        } else {
            int time = (countdown != 0) ? countdown : default_countdown;
            startMeditation(audio, time);
        }
    }

    private void pauseMeditation() {
        // Change text to "Play"
        updateButtonText();
        mediaPlayer.stop();
        // Perform other actions to pause the meditation session
    }

    private void startMeditation(int audio, int time) {
        // Change text to "Pause"
        isMeditationPlaying = true;

        if (audio != 0) {
            mediaPlayer = MediaPlayer.create(this, audio);
            mediaPlayer.setLooping(true);
            mediaPlayer.start(); // Start playing the audio

            // Schedule a Timer task to stop the audio after the specified duration
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    updateButtonText(); // Update the button text after stopping the audio
                }
            }, time);
        }
        updateButtonText();
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
        int time = (countdown != 0) ? countdown : default_countdown;
        if (isMeditationPlaying) {
            startMeditation(audio, time);
        }
        toggleMeditationState();
        updateButtonText();
    }
    private void playAudio(int audio) {
        // Release previous MediaPlayer instance if it exists
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        // Create new MediaPlayer instance and start playing the audio file
        mediaPlayer = MediaPlayer.create(this, audio);
        mediaPlayer.start();
    }
    private int resumeTimer(int countdown, int default_countdown, int elapsedTime) {
        int time = (countdown != 0) ? countdown : default_countdown;
        time -= elapsedTime;
        return time;
    }




    @Override
    public void onItemSelected(@NonNull AdapterView<?> parent, View view, int position, long id) {
        int spinnerID = parent.getId();
        // Handle item selection
        String selectedItem = parent.getItemAtPosition(position).toString();
        System.out.println(selectedItem);
        if (spinnerID == R.id.selectAudio) {
            audio = convertToAudio(selectedItem);
        } else if (spinnerID == R.id.timeSelector) {
            countdown = convertToTime(selectedItem);
        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle case where no item is selected (optional)
    }

    //{"Deep meditation", "Thunderstorm", "Relaxing birds and piano", "Spring breeze of meditation", "Silence"}
    private int convertToAudio(String selectedItem) {
        if(Objects.equals(selectedItem, "Deep meditation")) {
            return R.raw.deep_meditation;
        } else if (Objects.equals(selectedItem, "Thunderstorm")) {
            return R.raw.rain_and_thunder;
        } else if (Objects.equals(selectedItem, "Relaxing birds and piano")) {
            return R.raw.relaxing_birds_and_piano_music;
        } else if (Objects.equals(selectedItem, "Spring breeze of meditation")) {
            return R.raw.spring_breeze_of_meditation;
        } else {
            return 0;
        }
    }
    //{"5 min", "10 min", "15 min", "20 min", "25 min","30 min", "45min", "1 hr"}
    private int convertToTime(String selectedItem) {
        if(Objects.equals(selectedItem, "5 min")) {
            return 300000;
        } else if (Objects.equals(selectedItem, "10 min")) {
            return 600000;
        } else if (Objects.equals(selectedItem, "15 min")) {
            return 900000;
        }else if (Objects.equals(selectedItem, "20 min")) {
            return 1200000;
        } else if (Objects.equals(selectedItem, "25 min")) {
            return 1500000;
        } else if (Objects.equals(selectedItem, "30 min")) {
            return 1800000;
        } else if (Objects.equals(selectedItem, "45min")) {
            return 2700000;
        }else if (Objects.equals(selectedItem, "1 hr")) {
            return  3600000;
        } else  {
            return 0;
        }
    }
    // Method to pause the timer and audio playback
    private void pauseTimerAndAudio() {
        isPaused = true;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    // Method to resume the timer and audio playback
    /*private void resumeTimerAndAudio(int elapsedTime, int time) {
        isPaused = false;
        startTimer(); // Resume the timer
        if (mediaPlayer != null) {
            mediaPlayer.start(); // Resume audio playback
        }
    }*/
}












