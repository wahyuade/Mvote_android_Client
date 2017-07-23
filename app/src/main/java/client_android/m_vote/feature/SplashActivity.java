package client_android.m_vote.feature;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import client_android.m_vote.R;
import client_android.m_vote.service.SqliteDatabaseService;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Thread thread = new Thread(){
            public void run() {
                try{
                    sleep(0);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    SqliteDatabaseService db = new SqliteDatabaseService(SplashActivity.this);

                    if(db.checkVerifiedDevice()){
                        Intent login = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(login);
                    }else{
                        Intent verify = new Intent(SplashActivity.this, VerifyActivity.class);
                        startActivity(verify);
                    }
                    db.close();
                    finish();
                }
            }
        };
        thread.start();
    }
}
