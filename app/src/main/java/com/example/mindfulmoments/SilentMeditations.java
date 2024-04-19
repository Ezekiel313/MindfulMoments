package com.example.mindfulmoments;



import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Handler;
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


public class SilentMeditations extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {


    private boolean isMeditationPlaying = false;
    private Button playButton;
    private Button backButton;
    Timer timer = new Timer();
    private ProgressBar progressBar;

    // set default timer to 10 min in milliseconds
    int default_countdown = 600000;
    private int elapsedTime = 0;
    int countdown;
    private long startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_silent_meditations);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner timeSelector = findViewById(R.id.timeSelector);
        timeSelector.setOnItemSelectedListener(this);
        //create a list of items for the spinner.
        String[] times = new String[]{"1 min", "2 min", "3 min","4min","5 min", "10 min", "15 min", "20 min", "25 min","30 min", "45min", "1 hr"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, times);
//set the spinners adapter to the previously created one.
        timeSelector.setAdapter(timeAdapter);



        progressBar = findViewById(R.id.ProgressBar);
        playButton = findViewById(R.id.playButton);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources when the activity is destroyed

        if (timer != null) {
            timer.cancel();
        }
    }

    private void toggleMeditationState() {
        if (!isMeditationPlaying) {
            pauseMeditation();
        } else {
            int time = (countdown != 0) ? countdown : default_countdown;
            startMeditation(time);
        }
    }

    private void pauseMeditation() {
        // Change text to "Play"
        updateButtonText();
        // Perform other actions to pause the meditation session
        if (timer != null) {
            int time = resumeTimer(countdown, default_countdown, elapsedTime);
            timer.purge();
        }
    }

    private void startMeditation(final int time) {
        // Reset progress bar
        progressBar.setProgress(0);

        isMeditationPlaying = true;
        startTime = System.currentTimeMillis(); // Set the start time of the meditation

        // Initialize a Handler to update the UI with elapsed time
        Handler handler = new Handler();
        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isMeditationPlaying) {
                    // Calculate elapsed time
                    long elapsedTime = System.currentTimeMillis() - startTime;

                    // Calculate progress
                    int progress = (int) (elapsedTime * 100 / time); // Calculate progress as a percentage

                    // Update progress bar
                    progressBar.setProgress(progress);

                    // Schedule this Runnable to run again after a short delay
                    handler.postDelayed(this, 1000); // Update every second
                }
            }
        };

        // Start the initial update immediately
        handler.post(updateRunnable);

        // Schedule a Timer task to stop the audio after the specified duration
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(100);
                        isMeditationPlaying = false;
                        updateButtonText(); // Update the button text after stopping the audio
                        playCompletionSound();
                    }
                });
            }
        }, time);
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
            startMeditation(time);
        }

        toggleMeditationState();
        updateButtonText();
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
        if (spinnerID == R.id.timeSelector) {
            countdown = convertToTime(selectedItem);
        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle case where no item is selected (optional)
    }

    //{"1 min", "2 min", "3 min", "4min","5 min", "10 min", "15 min", "20 min", "25 min","30 min", "45min", "1 hr"}
    private int convertToTime(String selectedItem) {
        if (Objects.equals(selectedItem, "1 min")) {
            return 60000;
        }else if(Objects.equals(selectedItem, "2 min")) {
            return 120000;
        }else if(Objects.equals(selectedItem, "3 min")) {
            return 180000;
        }else if(Objects.equals(selectedItem, "4 min")) {
            return 240000;
        }else if(Objects.equals(selectedItem, "5 min")) {
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
    private void updateProgressBar(long elapsedTime, long totalTime) {
        // Calculate the progress percentage
        int progress = (int) ((float) elapsedTime / totalTime * 100);

        // Update the ProgressBar
        progressBar.setProgress(progress);
    }

    private void playCompletionSound() {
        final MediaPlayer completionMediaPlayer = MediaPlayer.create(this, R.raw.meditation_complete);
        completionMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                completionMediaPlayer.release(); // Release the MediaPlayer resources when the sound is completed
            }
        });
        completionMediaPlayer.start(); // Start playing the completion sound
    }

    private boolean meditationCompleted = false; // Add a boolean flag to track if meditation is completed

    // Modify your method to indicate when meditation is completed
    private void meditationCompleted() {
        if (!meditationCompleted) {
            meditationCompleted = true;
            playCompletionSound(); // Call the method to play the completion sound
        }
    }
    public void backButtonPressed(View view) {
        Intent intent = new Intent(SilentMeditations.this,MainActivity.class);
        startActivity(intent);
    }



}













