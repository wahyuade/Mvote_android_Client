package client_android.m_vote.feature;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import client_android.m_vote.R;
import client_android.m_vote.model.DefaultModel;
import client_android.m_vote.model.DeviceModel;
import client_android.m_vote.service.ApiServiceAdmin;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;

import client_android.m_vote.service.GPSTracker;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class Test extends Activity {
    private StompClient mStompClient;
    private static final String TAG = "MainActivity";
    private Gson mGson = new GsonBuilder().create();

    Button install, verify, login, vote;
    double latitude, longitude;
    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        install = (Button)findViewById(R.id.install);
        verify = (Button)findViewById(R.id.verify);
        login = (Button)findViewById(R.id.login);
        vote = (Button)findViewById(R.id.vote);

        mStompClient = Stomp.over(WebSocket.class, ApiServiceAdmin.SOCKET_URL+"/device/websocket");
        mStompClient.connect();
        gps = new GPSTracker(Test.this);

        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            gps.showSettingsAlert();
        }

        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceModel deviceModel = new DeviceModel();
                deviceModel.setUuid("i353453453");
                deviceModel.setStatus("0");
                deviceModel.setLatitude(latitude);
                deviceModel.setLongitude(longitude);
                mStompClient.send("/client/device_information",mGson.toJson(deviceModel)).subscribe();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceModel deviceModel = new DeviceModel();
                deviceModel.setUuid("i353453453");
                deviceModel.setStatus("1");
                deviceModel.setLatitude(latitude);
                deviceModel.setLongitude(longitude);
                mStompClient.send("/client/device_information",mGson.toJson(deviceModel)).subscribe();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceModel deviceModel = new DeviceModel();
                deviceModel.setUuid("i353453453");
                deviceModel.setStatus("2");
                deviceModel.setLatitude(latitude);
                deviceModel.setLongitude(longitude);
                mStompClient.send("/client/device_information",mGson.toJson(deviceModel)).subscribe();
            }
        });

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceModel deviceModel = new DeviceModel();
                deviceModel.setUuid("i353453453");
                deviceModel.setStatus("3");
                deviceModel.setLatitude(latitude);
                deviceModel.setLongitude(longitude);
                mStompClient.send("/client/device_information",mGson.toJson(deviceModel)).subscribe();
            }
        });
    }
    public void disconnectStomp(View view) {
        mStompClient.disconnect();
    }

    public void connectStomp() {

    }

    private void toast(String text) {
        Log.i(TAG, text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
