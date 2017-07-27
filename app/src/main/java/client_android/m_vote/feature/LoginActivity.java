package client_android.m_vote.feature;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.java_websocket.WebSocket;

import java.math.BigInteger;
import java.security.SecureRandom;

import client_android.m_vote.R;
import client_android.m_vote.feature.pilih_calon.CalonActivity;
import client_android.m_vote.model.DeviceModel;
import client_android.m_vote.model.LoginModel;
import client_android.m_vote.service.ApiServiceAdmin;
import client_android.m_vote.service.GPSTracker;
import client_android.m_vote.service.SqliteDatabaseService;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class LoginActivity extends Activity implements LocationListener {
    Button login_button;
    EditText nrp, token;
    BigInteger r;

    GPSTracker gps;
    private StompClient deviceClient;
    double latitude, longitude;
    private Gson JsonParser = new GsonBuilder().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = (Button)findViewById(R.id.login_button);
        nrp = (EditText)findViewById(R.id.nrp);
        token = (EditText)findViewById(R.id.token);



        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String data_nrp = nrp.getText().toString();
                String data_token = token.getText().toString();

                if(data_nrp.isEmpty() && data_token.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Mohon lengkapi nrp dan token Anda", Toast.LENGTH_SHORT).show();
                }else{
                    SecureRandom ran = new SecureRandom();
                    r = new BigInteger(498, 100, ran);

                    final SqliteDatabaseService db = new SqliteDatabaseService(LoginActivity.this);

                    String verified_nrp = db.getVerifiedData().getData().getNrp();
                    String verified_token = db.getVerifiedData().getData().getToken();
                    if(data_nrp.equals(verified_nrp) && data_token.equals(verified_token)){
                            ApiServiceAdmin.service_post.postLogin(RequestBody.create(MultipartBody.FORM, verified_nrp),RequestBody.create(MultipartBody.FORM, verified_token)).enqueue(new Callback<LoginModel>() {
                                @Override
                                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                                    if(response.isSuccessful()){
                                        if(response.body().isSuccess()){
                                            deviceClient = Stomp.over(WebSocket.class, ApiServiceAdmin.SOCKET_URL+"/device/websocket");
                                            deviceClient.connect();
                                            gps = new GPSTracker(LoginActivity.this);

                                            if(gps.canGetLocation()){
                                                latitude = gps.getLatitude();
                                                longitude = gps.getLongitude();
                                            }

                                            DeviceModel deviceModel = new DeviceModel();
                                            deviceModel.setUuid(db.getUUID());
                                            deviceModel.setStatus("2");
                                            deviceModel.setLatitude(latitude);
                                            deviceModel.setLongitude(longitude);
                                            deviceClient.send("/client/device_information",JsonParser.toJson(deviceModel)).subscribe();
                                            Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            Intent lihat_pilihan_calon = new Intent(LoginActivity.this, CalonActivity.class);
                                            lihat_pilihan_calon.putExtra("value_n", db.getKeyN());
                                            LoginActivity.this.startActivity(lihat_pilihan_calon);
                                            LoginActivity.this.finish();
                                        }else{
                                            Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }else{
                                        Toast.makeText(LoginActivity.this, "Mohon maaf terjadi kesalahan pada jaringan Anda", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<LoginModel> call, Throwable t) {
                                    Toast.makeText(LoginActivity.this, "Mohon maaf terjadi gangguan pada jaringan Anda", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }else{
                        Toast.makeText(LoginActivity.this, "gagal gan", Toast.LENGTH_SHORT).show();
                    }
                    db.close();
//
//                    ApiServiceAdmin.service_post.postLogin(RequestBody.create(MultipartBody.FORM, data_nrp), RequestBody.create(MultipartBody.FORM, data_token)).enqueue(new Callback<LoginModel>() {
//                        @Override
//                        public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
//                            if(response.isSuccessful()){
//                                if(response.body().isSuccess()){
//                                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

//                                }else{
//                                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }else {
//                                Toast.makeText(LoginActivity.this, Integer.toString(response.code()), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<LoginModel> call, Throwable t) {
//                            Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Lat", Double.toString(location.getLatitude()));
        Log.d("Long", Double.toString(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
