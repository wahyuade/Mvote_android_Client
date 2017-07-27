package client_android.m_vote.feature.pilih_calon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.java_websocket.WebSocket;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import client_android.m_vote.R;
import client_android.m_vote.feature.LoginActivity;
import client_android.m_vote.model.CalonModel;
import client_android.m_vote.model.ChallangeModel;
import client_android.m_vote.model.DefaultModel;
import client_android.m_vote.model.DeviceModel;
import client_android.m_vote.service.ApiServiceAdmin;
import client_android.m_vote.library.BCrypt;
import client_android.m_vote.service.GPSTracker;
import client_android.m_vote.service.SqliteDatabaseService;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class CalonActivity extends Activity {
    String value_n;
    GridView list_calon;
    ListCalonGridAdapter adapter;
    String nrp;
    String h;
    String id_calon;
    String uuid;

    BigInteger x;
    BigInteger r;
    BigInteger s;
    BigInteger n;

    GPSTracker gps;
    private StompClient deviceClient;
    double latitude, longitude;
    private Gson JsonParser = new GsonBuilder().create();

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calon);

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu..");
        loading.show();

        Thread thread = new Thread(){
            public void run() {
                try{
                    sleep(180000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    finish();
                }
            }
        };

        thread.start();

        list_calon = (GridView)findViewById(R.id.list_calon);

        value_n = getIntent().getStringExtra("value_n");

        ApiServiceAdmin.service_get.getListCalon(value_n).enqueue(new Callback<ArrayList<CalonModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CalonModel>> call, final Response<ArrayList<CalonModel>> response) {
                if(response.isSuccessful()){
                    loading.dismiss();
                    if(response.body() != null){
                        SqliteDatabaseService db = new SqliteDatabaseService(CalonActivity.this);

                        SecureRandom ran = new SecureRandom();
                        r = new BigInteger(498, 100, ran);

                        s = new BigInteger(db.getKeyPrivat());
                        uuid = db.getUUID();
                        n = new BigInteger(db.getVerifiedData().getData().getN());
                        x = (r.multiply(r)).mod(n);
                        nrp = db.getKeyNrp();

                        db.close();

                        adapter = new ListCalonGridAdapter(response.body(),CalonActivity.this, value_n);
                        list_calon.setAdapter(adapter);

                        list_calon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                final AlertDialog detail_calon  = new AlertDialog.Builder(CalonActivity.this).create();
                                detail_calon.setView(getLayoutInflater().inflate(R.layout.detail_calon, null));
                                detail_calon.show();
                                TextView nomor_urut = (TextView) detail_calon.findViewById(R.id.nomor_urut);
                                TextView nama_calon = (TextView) detail_calon.findViewById(R.id.nama_calon);
                                TextView visi_calon = (TextView) detail_calon.findViewById(R.id.visi_calon);
                                TextView misi_calon = (TextView) detail_calon.findViewById(R.id.misi_calon);
                                ImageView foto_calon = (ImageView) detail_calon.findViewById(R.id.foto_calon);
                                Glide.with(CalonActivity.this).load(ApiServiceAdmin.BASE_URL+"/calon/"+response.body().get(i).getFoto()).into(foto_calon);
                                Button batal = (Button)detail_calon.findViewById(R.id.batal);
                                Button vote = (Button)detail_calon.findViewById(R.id.vote);

                                nomor_urut.setText(response.body().get(i).getId());
                                nama_calon.setText(response.body().get(i).getNama());
                                visi_calon.setText(response.body().get(i).getVisi());
                                misi_calon.setText(response.body().get(i).getMisi());


                                batal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        detail_calon.dismiss();
                                    }
                                });
                                vote.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder vote_calon = new AlertDialog.Builder(CalonActivity.this);
                                        vote_calon.setTitle("Perhatian");
                                        vote_calon.setMessage("Apakah Anda yakin untuk memilih "+response.body().get(i).getNama()+" ?");
                                        id_calon = response.body().get(i).getId();
                                        vote_calon.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                loading.show();
                                                //Mendapatkan nilai challange
                                                ApiServiceAdmin.service_post.postCheck_m(RequestBody.create(MultipartBody.FORM, value_n), RequestBody.create(MultipartBody.FORM, x.toString()), RequestBody.create(MultipartBody.FORM, BCrypt.hashpw(id_calon, BCrypt.gensalt()))).enqueue(new Callback<ChallangeModel>() {
                                                    @Override
                                                    public void onResponse(Call<ChallangeModel> call, Response<ChallangeModel> response) {
                                                        if(response.body().isSuccess()){
                                                            BigInteger y = r.multiply((s).pow(response.body().getC())).mod(n);

                                                            Log.d("x", x.toString());
                                                            Log.d("y", y.toString());
                                                            h = BCrypt.hashpw(id_calon, BCrypt.gensalt());
                                                            Log.d("h", h);
                                                            Log.d("c", Integer.toString(response.body().getC()));
                                                            Log.d("s", s.toString());
                                                            Log.d("n", n.toString());

                                                            ApiServiceAdmin.service_post.postVoteValidate(
                                                                    RequestBody.create(MultipartBody.FORM, nrp),
                                                                    RequestBody.create(MultipartBody.FORM, y.toString()),
                                                                    RequestBody.create(MultipartBody.FORM, h),
                                                                    RequestBody.create(MultipartBody.FORM, Integer.toString(response.body().getC()))).enqueue(new Callback<DefaultModel>() {
                                                                @Override
                                                                public void onResponse(Call<DefaultModel> call, Response<DefaultModel> response) {
                                                                    detail_calon.dismiss();

                                                                    deviceClient = Stomp.over(WebSocket.class, ApiServiceAdmin.SOCKET_URL+"/device/websocket");
                                                                    deviceClient.connect();
                                                                    gps = new GPSTracker(CalonActivity.this);

                                                                    if(gps.canGetLocation()){
                                                                        latitude = gps.getLatitude();
                                                                        longitude = gps.getLongitude();
                                                                    }

                                                                    DeviceModel deviceModel = new DeviceModel();
                                                                    deviceModel.setUuid(uuid);
                                                                    deviceModel.setStatus("3");
                                                                    deviceModel.setLatitude(latitude);
                                                                    deviceModel.setLongitude(longitude);
                                                                    deviceClient.send("/client/device_information",JsonParser.toJson(deviceModel)).subscribe();

                                                                    Toast.makeText(CalonActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                                                    Intent login_activity = new Intent(CalonActivity.this, LoginActivity.class);
                                                                    CalonActivity.this.startActivity(login_activity);
                                                                    CalonActivity.this.finish();
                                                                    loading.dismiss();
                                                                }

                                                                @Override
                                                                public void onFailure(Call<DefaultModel> call, Throwable t) {
                                                                    Toast.makeText(CalonActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }else{
                                                            loading.dismiss();
                                                            Toast.makeText(CalonActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }

                                                    }

                                                    @Override
                                                    public void onFailure(Call<ChallangeModel> call, Throwable t) {
                                                        Toast.makeText(CalonActivity.this, "Mohon maaf terjadi gangguan jaringan dengan device Anda", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        vote_calon.show();
                                    }
                                });
                            }
                        });
                    }else {
                        Toast.makeText(CalonActivity.this, "Mohon maaf terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CalonActivity.this, "Mohon maaf terjadi gangguan dengan jaringan Anda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CalonModel>> call, Throwable t) {

            }
        });

    }
}

