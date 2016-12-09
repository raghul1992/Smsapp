package twilightsoftwares.com.smsapptest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import twilightsoftwares.com.smsapptest.R;

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context=this;



        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {


                    Intent i = new Intent(SplashActivity.this, SmsActivity.class);
                    startActivity(i);
                    finish();

                }


        }, SPLASH_TIME_OUT);
    }

}
//826941459991-q79bu2dcpfgr28huufeva6ajlb6j05s9.apps.googleusercontent.com
