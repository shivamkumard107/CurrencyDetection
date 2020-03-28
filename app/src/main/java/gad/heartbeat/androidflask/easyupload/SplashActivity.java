package gad.heartbeat.androidflask.easyupload;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

                boolean welcome = sharedPref.getBoolean("welcome", false);
                boolean permission = sharedPref.getBoolean("permission", false);
                boolean instructions = sharedPref.getBoolean("instruction", false);
                if (!welcome) {
                    Intent i = new Intent(SplashActivity.this,
                            WelcomeScreenActivity.class);
                    startActivity(i);
                } else if (!permission) {
                    Intent i = new Intent(SplashActivity.this,
                            AskPermissionActivity.class);
                    startActivity(i);
                } else if (!instructions) {
                    Intent i = new Intent(SplashActivity.this,
                            InstructionActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashActivity.this,
                            TakePictureActivity.class);
                    startActivity(i);
                }
                //invoke the SecondActivity.

                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }
}
