package client_android.m_vote.feature;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.java_websocket.WebSocket;

import client_android.m_vote.R;
import client_android.m_vote.model.DeviceModel;
import client_android.m_vote.model.VerifyModel;
import client_android.m_vote.service.ApiServiceAdmin;
import client_android.m_vote.service.ApiServiceTTP;
import client_android.m_vote.service.GPSTracker;
import client_android.m_vote.service.SqliteDatabaseService;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class VerifyActivity extends Activity{
    Button verify;
    EditText code_verification;
    public static final int MY_PERMISSIONS_LOCATION = 123;
    GPSTracker gps;
    private StompClient deviceClient;
    double latitude, longitude;
    private Gson JsonParser = new GsonBuilder().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        verify = (Button)findViewById(R.id.verify);
        code_verification = (EditText)findViewById(R.id.code_verification);

        checkPermission();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kode_verifikasi = code_verification.getText().toString();
                if(!kode_verifikasi.isEmpty() && checkPermission()){
                    ApiServiceTTP.service_post.postVerify(RequestBody.create(MultipartBody.FORM, kode_verifikasi)).enqueue(new Callback<VerifyModel>() {
                        @Override
                        public void onResponse(Call<VerifyModel> call, Response<VerifyModel> response) {
                            if(response.isSuccessful()){
                                if(response.body().isSuccess()){
                                    Toast.makeText(VerifyActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    SqliteDatabaseService db = new SqliteDatabaseService(VerifyActivity.this);
                                    if(db.verifyDpt(response.body())){
                                        db.generateUUID();
                                        String uuid = db.getUUID();
                                        db.close();
                                        deviceClient = Stomp.over(WebSocket.class, ApiServiceAdmin.SOCKET_URL+"/device/websocket");
                                        deviceClient.connect();
                                        gps = new GPSTracker(VerifyActivity.this);

                                        if(gps.canGetLocation()){
                                            latitude = gps.getLatitude();
                                            longitude = gps.getLongitude();
                                        }

                                        DeviceModel deviceModel = new DeviceModel();
                                        deviceModel.setUuid(uuid);
                                        deviceModel.setStatus("1");
                                        deviceModel.setLatitude(latitude);
                                        deviceModel.setLongitude(longitude);
                                        deviceClient.send("/client/device_information",JsonParser.toJson(deviceModel)).subscribe();


                                        Intent login_activity = new Intent(VerifyActivity.this, LoginActivity.class);
                                        startActivity(login_activity);
                                    }
                                }else{
                                    Toast.makeText(VerifyActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(VerifyActivity.this, "Mohon maaf terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<VerifyModel> call, Throwable t) {

                        }
                    });
                }else{
                    Toast.makeText(gps, "Anda harus memasukkan kode verifikasi Anda dan mengijinkan App menggunakan GPS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(VerifyActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)VerifyActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(VerifyActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Allow to access location ?");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)VerifyActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_LOCATION);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity)VerifyActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_LOCATION);
                }
                if (ContextCompat.checkSelfPermission(VerifyActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }else{
                    return false;
                }

            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
