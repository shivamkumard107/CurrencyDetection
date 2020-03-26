package gad.heartbeat.androidflask.easyupload;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class WelcomeScreenActivity extends AppCompatActivity {
    private MediaPlayer mPlayer_en, mPlayer_hi;
    private Button btSkip;
    private Vibrator mVib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        mVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        //calling audio to play audio instructions
        audioPlayer();

        //go to next activity if skip button pressed
        btSkip = findViewById(R.id.bt_skip_welcome);
        btSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer_en.stop();
                mPlayer_hi.stop();
                mVib.vibrate(50);
                startActivity(new Intent(WelcomeScreenActivity.this, AskPermissionActivity.class));
            }
        });

    }
    public void audioPlayer(){
        //set up MediaPlayer
        mPlayer_en= MediaPlayer.create(WelcomeScreenActivity.this, R.raw.welcome_en);
        mPlayer_hi = MediaPlayer.create(WelcomeScreenActivity.this, R.raw.welcome_hi);
        mPlayer_en.start();
        mPlayer_en.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //start MediaPlayer in hindi
                mPlayer_hi.start();
            }
        });
        mPlayer_hi.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                startActivity(new Intent(WelcomeScreenActivity.this, AskPermissionActivity.class));
            }
        });

    }
}
