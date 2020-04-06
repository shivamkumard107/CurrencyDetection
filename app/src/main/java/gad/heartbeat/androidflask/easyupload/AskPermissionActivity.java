package gad.heartbeat.androidflask.easyupload;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AskPermissionActivity extends AppCompatActivity {
    private MediaPlayer mPlayer_en, mPlayer_hi;
    private Button btSkip;
    private Vibrator mVib;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_VIBRATE = 2;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_AUDIO = 4;

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
                if (checkAndRequestPermissions()) {
                    SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("permission", true);
                    editor.apply();
                    startActivity(new Intent(AskPermissionActivity.this, InstructionActivity.class));
                    finish();
                }
            }
        });
    }

    public void audioPlayer() {
        //set up MediaPlayer
        mPlayer_en = MediaPlayer.create(AskPermissionActivity.this, R.raw.permission_en);
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
                if (checkAndRequestPermissions()) {
                    SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("permission", true);
                    editor.apply();
                    startActivity(new Intent(AskPermissionActivity.this, InstructionActivity.class));
                    finish();
                }
            }
        });

    }

    private boolean checkAndRequestPermissions() {
        int permissionStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (audioPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPlayer_en.stop();
        mPlayer_hi.stop();
    }
}
