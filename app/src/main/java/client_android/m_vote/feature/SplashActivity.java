package client_android.m_vote.feature;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import client_android.m_vote.R;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Thread thread = new Thread(){
            public void run() {
                try{
                    sleep(5000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    Intent login = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(login);
                    finish();
                }
            }
        };
        thread.start();
    }
}
