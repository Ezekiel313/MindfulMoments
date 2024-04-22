package com.example.mindfulmoments;


import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meditation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        ConstraintLayout main = findViewById(R.id.main);

        int color = ((ColorDrawable) main.getBackground()).getColor();
        toolbar.setBackgroundColor(color);// Set the title text color after setting support action bar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
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

        Spinner audioSelector = findViewById(R.id.selectAudio);
        audioSelector.setOnItemSelectedListener(this);
        //create a list of items for the spinner.
        String[] audioOptions = new String[] {"Deep Meditation", "Thunderstorm", "Relaxing Birds and Piano", "Spring Breeze of Meditation", "Healing Forest", "Garden Serenity", "Eastern Journey", "Morning in the Mountains"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> audioAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, audioOptions);
//set the spinners adapter to the previously created one.
        audioSelector.setAdapter(audioAdapter);

        progressBar = findViewById(R.id.ProgressBar);
        playButton = findViewById(R.id.playButton);

    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to the previous screen
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (!isMeditationPlaying) {
            pauseMeditation();
        } else {
            int time = (countdown != 0) ? countdown : default_countdown;
            startMeditation(audio, time);
        }
    }

    private void pauseMeditation() {
        // Change text to "Play"
        updateButtonText();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        // Perform other actions to pause the meditation session
        if (timer != null) {
            int time = resumeTimer(countdown, default_countdown, elapsedTime);
            timer.purge();
        }
    }

    private void startMeditation(int audio, int time) {
        //reset progress bar
        progressBar.setProgress(0);

        isMeditationPlaying = true;
        final boolean[] isReleased = {false};

        // Release any existing MediaPlayer instance
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (audio != 0) {
            mediaPlayer = MediaPlayer.create(this, audio);
            mediaPlayer.setLooping(true);
            mediaPlayer.start(); // Start playing the audio

            // Initialize a Handler to update the UI with elapsed time
            Handler handler = new Handler();
            Runnable updateRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isMeditationPlaying) {
                        long elapsedTime = mediaPlayer.getCurrentPosition(); // Get current playback position
                        // Update UI with elapsed time if needed
                        updateProgressBar(elapsedTime, time);

                        // Schedule this Runnable to run again after a short delay
                        handler.postDelayed(this, 1000); // Update every second
                    }
                }
            };
            // Start the initial update immediately
            handler.post(updateRunnable);


            // Schedule a Timer task to stop the audio after the specified duration
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null && !isReleased[0]) {
                                progressBar.setProgress(100);
                                mediaPlayer.pause();
                                mediaPlayer.release();
                                mediaPlayer = null;
                                isReleased[0] = true;
                                isMeditationPlaying = false;
                            }
                            updateButtonText(); // Update the button text after stopping the audio
                            playCompletionSound();
                        }
                    });
                }
            }, time);



        }
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

    //{"Deep Meditation", "Thunderstorm", "Relaxing Birds and Piano", "Spring Breeze of Meditation", "Healing Forest", "Garden Serenity", "Eastern Journey", "Morning in the Mountains"};
    private int convertToAudio(String selectedItem) {
        if(Objects.equals(selectedItem, "Deep Meditation")) {
            return R.raw.deep_meditation;
        } else if (Objects.equals(selectedItem, "Thunderstorm")) {
            return R.raw.rain_and_thunder;
        } else if (Objects.equals(selectedItem, "Relaxing Birds and Piano")) {
            return R.raw.relaxing_birds_and_piano_music;
        } else if (Objects.equals(selectedItem, "Spring Breeze of Meditation")) {
            return R.raw.spring_breeze_of_meditation;
        } else if (Objects.equals(selectedItem, "Healing Forest")) {
            return R.raw.healing_forest;
        }else if (Objects.equals(selectedItem, "Garden Serenity")) {
            return R.raw.garden_serenity;
        }else if (Objects.equals(selectedItem,  "Eastern Journey")) {
            return R.raw.eastern_meditative;
        }else if (Objects.equals(selectedItem,  "Morning in the Mountains")) {
        return R.raw.morning_in_the_mountains;
        } else {
            return 0;
        }
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
    private boolean hasCompleted = false;

    private void playCompletionSound() {
        MediaPlayer completionMediaPlayer = MediaPlayer.create(this, R.raw.meditation_complete);
        completionMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!hasCompleted) {
                    hasCompleted = true;
                    mp.release(); // Release the MediaPlayer resources when the sound is completed
                }
            }
        });
        completionMediaPlayer.start(); // Start playing the completion sound

        // Delay the sound effect by 6.5 seconds
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the completion listener has already been triggered
                if (!hasCompleted) {
                    // Create a new MediaPlayer instance for the delayed sound effect
                    MediaPlayer delayedMediaPlayer = MediaPlayer.create(Meditation.this, R.raw.meditation_complete);
                    delayedMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release(); // Release the MediaPlayer resources when the sound is completed
                        }
                    });
                    delayedMediaPlayer.start(); // Play the delayed sound effect
                }
            }
        }, 1500); // Delay for 1.5 sec
    }



}












