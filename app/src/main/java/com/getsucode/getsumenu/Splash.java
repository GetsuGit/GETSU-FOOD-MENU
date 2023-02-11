package com.getsucode.getsumenu;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        (new CountDownTimer(1000L, 800L) {
            public void onFinish() {
                Intent intent = new Intent(Splash.this.getBaseContext(), MainActivity.class);
                Splash.this.startActivity(intent);
                Splash.this.finish();
            }

            public void onTick(long millisUntilFinished) {
            }
        }).start();
    }
}