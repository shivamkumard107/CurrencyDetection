package gad.heartbeat.androidflask.easyupload;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AskPermissionActivity extends AppCompatActivity {
    private MediaPlayer mPlayer_en, mPlayer_hi;
    private Button btSkip;
    private Vibrator mVib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_permission);
        mVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        //calling audio to play audio instructions
        audioPlayer();

        //go to next activity if skip button pressed
        btSkip = findViewById(R.id.bt_skip_permission);
        btSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer_en.stop();
                mPlayer_hi.stop();
                mVib.vibrate(50);
                //TODO: check and ask for permission here then proceed to next activity

                SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("permission", true);
                editor.apply();
                startActivity(new Intent(AskPermissionActivity.this, InstructionActivity.class));
                finish();
            }
        });
    }
    public void audioPlayer(){
        //set up MediaPlayer
        mPlayer_en= MediaPlayer.create(AskPermissionActivity.this, R.raw.permission);
        mPlayer_hi = MediaPlayer.create(AskPermissionActivity.this, R.raw.permission_hi);
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
                SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("permission", true);
                editor.apply();
                startActivity(new Intent(AskPermissionActivity.this, InstructionActivity.class));
                finish();
            }
        });

    }
}
