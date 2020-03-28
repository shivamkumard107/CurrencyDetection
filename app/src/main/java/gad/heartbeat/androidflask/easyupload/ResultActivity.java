package gad.heartbeat.androidflask.easyupload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static gad.heartbeat.androidflask.easyupload.TakePictureActivity.BITMAP_SAMPLE_SIZE;

public class ResultActivity extends AppCompatActivity {
    private String currency_path;
    String noteValue;

    private ImageView imgPreview;
    private TextView tvNoteValue;
    private ImageView ivSpeaker;
    private Button btScanAnother;
    private Vibrator mVib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mVib = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        imgPreview = findViewById(R.id.iv_bank_note);
        tvNoteValue = findViewById(R.id.tv_note_value);
        ivSpeaker = findViewById(R.id.iv_speak);
        btScanAnother = findViewById(R.id.bt_scan);

         noteValue = getIntent().getStringExtra("note_value");
        currency_path = getIntent().getStringExtra("note_image");
        previewCapturedImage();
        tvNoteValue.setText(noteValue);
        audioPlayer(noteValue);
        ivSpeaker.setImageResource(R.drawable.ic_stop_speak);

        //onclick listeners
        ivSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVib.vibrate(50);
                ivSpeaker.setImageResource(R.drawable.ic_speak);
                audioPlayer(noteValue);
                ivSpeaker.setImageResource(R.drawable.ic_stop_speak);
            }
        });
        btScanAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVib.vibrate(50);
                Intent intent = new Intent(ResultActivity.this, TakePictureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
    private void previewCapturedImage() {
        try {
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, currency_path);
            imgPreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public void audioPlayer(String note) {
        //set up MediaPlayer
        try {
            MediaPlayer mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.speech);
            switch (note) {
                case "10":
                case "10_1.":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a10);
                    break;
                case "10back":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a10back);
                    break;
                case "10_new":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a10_new);
                    break;
                case "10_newback":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a10_newback);
                    break;
                case "20":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a20);
                    break;
                case "20back":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a20back);
                    break;
                case "20_new":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a20_new);
                    break;
                case "20_newback":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a20_newback);
                    break;
                case "50":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a50);
                    break;
                case "50back":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a50back);
                    break;
                case "50_new":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a50_new);
                    break;
                case "50_newback":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a50_newback);
                    break;
                case "100":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a100);
                    break;
                case "100back":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a100back);
                    break;
                case "100_new":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a100_new);
                    break;
                case "100_newback":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a100_newback);
                    break;
                case "200":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a200);
                    break;
                case "500":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a500);
                    break;
                case "500back":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a500back);
                    break;
                case "500_1":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a500_1);
                    break;
                case "500_2":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a500_2);
                    break;
                case "2000":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a2000);
                    break;
                case "2000back":
                    mPlayer = MediaPlayer.create(ResultActivity.this, R.raw.a2000back);
                    break;

            }
            mPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
