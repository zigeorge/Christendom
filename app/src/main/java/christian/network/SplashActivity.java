package christian.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import christian.network.services.MyFirebaseInstanceIdService;
import christian.network.utils.ApplicationUtility;
import christian.network.utils.StaticData;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ApplicationUtility.printKeyHash(this);
        final boolean isSessionExists = getSesionFromPref();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isSessionExists) {
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                    finish();
                } else {
                    String church_id = getSharedPreferences(StaticData.APP_PREFERENCE,Context.MODE_APPEND).getString(StaticData.CHURCH_ID,"0");
                    if(church_id.equalsIgnoreCase("0")){
                        startActivity(new Intent(SplashActivity.this, SelectchurchActivity.class));
                    }else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                    finish();
                }
            }
        }, 1000);
    }

    private boolean getSesionFromPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(StaticData.APP_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(StaticData.SESSION, false);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}