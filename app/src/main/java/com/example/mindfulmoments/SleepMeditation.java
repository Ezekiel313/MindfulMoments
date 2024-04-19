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

public class SleepMeditation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


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
        setContentView(R.layout.activity_sleep_meditation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner timeSelector = findViewById(R.id.timeSelector);
        timeSelector.setOnItemSelectedListener(this);
        //create a list of items for the spinner.
        String[] times = new String[]{"1 min", "1 hr", "2 hr", "3 hr","4 hr","5 hr","6 hr","7 hr","8 hr","9 hr","10 hr", "12hr", "16hr" };
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, times);
//set the spinners adapter to the previously created one.
        timeSelector.setAdapter(timeAdapter);

        Spinner audioSelector = findViewById(R.id.selectAudio);
        audioSelector.setOnItemSelectedListener(this);
        //create a list of items for the spinner.
        String[] audioOptions = new String[] {"returning dreams", "Thunderstorm", "healing forest", "eastern meditative", "Deep meditation", "sleep serenity", "spring breeze of meditation", "Deep sleep", "rain in paradise", "lullaby of the rain"};
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
        // Reset progress bar
        progressBar.setProgress(0);

        isMeditationPlaying = true;

        // Release any existing MediaPlayer instance
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (audio != 0) {
            try {
                mediaPlayer = MediaPlayer.create(this, audio);
                mediaPlayer.start(); // Start playing the audio

                final int totalTime = time;

                // Initialize a Handler to update the UI with elapsed time
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null && isMeditationPlaying) {
                            int elapsedTime = mediaPlayer.getCurrentPosition(); // Get current playback position
                            int progress = (int) (elapsedTime * 100.0 / totalTime); // Calculate progress percentage
                            progressBar.setProgress(progress);

                            // Schedule this Runnable to run again after a short delay
                            handler.postDelayed(this, 1000); // Update every second
                        }
                    }
                });

                // Schedule a Timer task to stop the audio after the specified duration
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mediaPlayer != null && isMeditationPlaying) {
                                    mediaPlayer.pause();
                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                    isMeditationPlaying = false;
                                    progressBar.setProgress(100); // Ensure progress reaches 100%
                                    updateButtonText(); // Update the button text after stopping the audio
                                    playCompletionSound();
                                }
                            }
                        });
                    }
                }, time);

                // Update progress bar continuously based on playback position
                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                        int elapsedTime = mediaPlayer.getCurrentPosition(); // Get current playback position
                        int progress = (int) (elapsedTime * 100.0 / totalTime); // Calculate progress percentage using time
                        progressBar.setProgress(progress);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
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

    //{"returning dreams", "Thunderstorm", "healing forest", "eastern meditative", "Deep meditation", "sleep serenity", "spring breeze of meditation", "Deep sleep"
    private int convertToAudio(String selectedItem) {
        if(Objects.equals(selectedItem, "Deep meditation")) {
            return R.raw.deep_meditation;
        } else if (Objects.equals(selectedItem, "Thunderstorm")) {
            return R.raw.rain_and_thunder;
        } else if (Objects.equals(selectedItem, "Spring breeze of meditation")) {
            return R.raw.spring_breeze_of_meditation;
        } else if (Objects.equals(selectedItem, "healing forest")) {
            return R.raw.healing_forest;
        }else if (Objects.equals(selectedItem,  "returning dreams")) {
            return R.raw.returning_dreams;
        }else if (Objects.equals(selectedItem,  "Deep sleep")) {
            return R.raw.deep_sleep;
        }else if (Objects.equals(selectedItem,  "sleep serenity")) {
            return R.raw.sleep_serenity;
        }else if (Objects.equals(selectedItem,  "rain in paradise")) {
            return R.raw.rain_in_paradise;
        }else if (Objects.equals(selectedItem,  "lullaby of the rain")) {
            return R.raw.lullaby_of_the_rain;
        }
        else {
            return 0;
        }
    }
    //{"1 min", "1 hr", "2 hr", "3 hr","4 hr","5 hr","6 hr","7 hr","8 hr","9 hr","10 hr", "12 hr" }
    private int convertToTime(String selectedItem) {
        if (Objects.equals(selectedItem, "1 min")) {
            return 60000;
        }else if(Objects.equals(selectedItem, "1 hr")) {
            return 3600000;
        }else if(Objects.equals(selectedItem, "2 hr")) {
            return 7200000;
        }else if(Objects.equals(selectedItem, "3 hr")) {
            return 10800000;
        }else if(Objects.equals(selectedItem, "4 hr")) {
            return 14400000;
        } else if (Objects.equals(selectedItem, "5 hr")) {
            return 18000000;
        } else if (Objects.equals(selectedItem, "6 hr")) {
            return 21600000;
        }else if (Objects.equals(selectedItem, "7 hr")) {
            return 25200000;
        } else if (Objects.equals(selectedItem, "8 hr")) {
            return 28800000;
        } else if (Objects.equals(selectedItem, "9 hr")) {
            return 32400000;
        } else if (Objects.equals(selectedItem, "10 hr")) {
            return 36000000;
        }else if (Objects.equals(selectedItem, "12 hr")) {
            return  43200000;
        }else if (Objects.equals(selectedItem, "16 hr")) {
            return 57600000;
        }else  {
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
        final MediaPlayer completionMediaPlayer = MediaPlayer.create(this, R.raw.meditation_complete);
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

        // Delay the sound effect by 6.5 seconds if not completed
        if (!hasCompleted) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!hasCompleted) {
                        completionMediaPlayer.release(); // Release the MediaPlayer resources
                    }
                }
            }, 6500); // Delay for 6.5 seconds
        }
    }

    public void backButtonPressed(View view) {
        Intent intent = new Intent(SleepMeditation.this,MainActivity.class);
        startActivity(intent);
    }


}












