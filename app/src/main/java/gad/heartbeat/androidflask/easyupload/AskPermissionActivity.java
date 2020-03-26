package gad.heartbeat.androidflask.easyupload;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AskPermissionActivity extends AppCompatActivity {
    private MediaPlayer mPlayer_en, mPlayer_hi;
    private Button btSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_permission);
        audioPlayer();
        btSkip = findViewById(R.id.bt_skip_permission);
        btSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer_en.stop();
                mPlayer_hi.stop();
                //TODO: ask for permission here then proceed to next activity
                startActivity(new Intent(AskPermissionActivity.this, InstructionActivity.class));
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
                startActivity(new Intent(AskPermissionActivity.this, InstructionActivity.class));
            }
        });

    }
}
