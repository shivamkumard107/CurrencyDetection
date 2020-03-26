package gad.heartbeat.androidflask.easyupload;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InstructionActivity extends AppCompatActivity {
    private MediaPlayer mPlayer_en, mPlayer_hi;
    private Button btSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        audioPlayer();

        btSkip = findViewById(R.id.bt_skip_instructions);
        btSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer_en.stop();
                mPlayer_hi.stop();
                Intent i = new Intent(InstructionActivity.this, TakePictureActivity.class);
                //TODO: add flags to clear top
                startActivity(i);
            }
        });
    }
    public void audioPlayer(){
        //set up MediaPlayer
        mPlayer_en= MediaPlayer.create(InstructionActivity.this, R.raw.instructions_en);
        mPlayer_hi = MediaPlayer.create(InstructionActivity.this, R.raw.instructions_hi);
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
                startActivity(new Intent(InstructionActivity.this, TakePictureActivity.class));
            }
        });

    }
}
