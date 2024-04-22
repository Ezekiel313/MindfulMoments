package com.example.mindfulmoments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import android.os.Handler;

/*The purpose of this class is to rotate through a string array of affirmations
  in the affirmations activity */
public class Affirmations extends AppCompatActivity {
    private TextView affirmationsText;
    private String[] affirmationStrings;
    private int currentIndex = 0;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affirmations);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        ConstraintLayout main = findViewById(R.id.main);

        int color = ((ColorDrawable) main.getBackground()).getColor();
        toolbar.setBackgroundColor(color);// Set the title text color after setting support action bar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        affirmationsText = findViewById(R.id.affirmationsTextView);
        handler = new Handler();

        affirmationStrings = getResources().getStringArray(R.array.affirmationStrings);

        updateTextViewWithArray();
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

    /* This method allows for the string array to be used to update text view
    with the affirmations array.
     */
    private void updateTextViewWithArray(){
        affirmationsText.setText(affirmationStrings[currentIndex]);

        currentIndex = (currentIndex + 1) % affirmationStrings.length;

        /* Handler is used to keep updateTextViewWithArray() from running until
           it has been 8 seconds.
        */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTextViewWithArray();
            }
        }, 8000);
    }
    /* onDestroy() removes any messages that were displayed when the activity
       is destroyed
     */
    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
