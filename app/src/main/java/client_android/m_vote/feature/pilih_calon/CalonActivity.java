package client_android.m_vote.feature.pilih_calon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import client_android.m_vote.R;
import client_android.m_vote.feature.LoginActivity;
import client_android.m_vote.model.CalonModel;
import client_android.m_vote.model.DefaultModel;
import client_android.m_vote.service.ApiService;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalonActivity extends Activity {
    String local;
    GridView list_calon;
    ListCalonGridAdapter adapter;

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
                    sleep(300000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    finish();
                }
            }
        };

        thread.start();

        list_calon = (GridView)findViewById(R.id.list_calon);

        local = getIntent().getStringExtra("local");

        ApiService.service_get.getListCalon(local).enqueue(new Callback<ArrayList<CalonModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CalonModel>> call, final Response<ArrayList<CalonModel>> response) {
                if(response.isSuccessful()){
                    loading.dismiss();
                    if(response.body() != null){
                        adapter = new ListCalonGridAdapter(response.body(),CalonActivity.this, local);
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
                                Button batal = (Button)detail_calon.findViewById(R.id.batal);
                                Button vote = (Button)detail_calon.findViewById(R.id.vote);

                                nomor_urut.setText(response.body().get(i).getId());
                                nama_calon.setText(response.body().get(i).getNama());
                                visi_calon.setText(response.body().get(i).getVisi());
                                misi_calon.setText(response.body().get(i).getMisi());

                                foto_calon.setImageResource(adapter.presiden[i]);

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
                                        final String id_calon = response.body().get(i).getId();
                                        vote_calon.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                ApiService.service_post.postVote(RequestBody.create(MultipartBody.FORM, local), RequestBody.create(MultipartBody.FORM, id_calon)).enqueue(new Callback<DefaultModel>() {
                                                    @Override
                                                    public void onResponse(Call<DefaultModel> call, Response<DefaultModel> response) {
                                                        Toast.makeText(CalonActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                                        Intent login_activity = new Intent(CalonActivity.this, LoginActivity.class);
                                                        CalonActivity.this.startActivity(login_activity);
                                                        CalonActivity.this.finish();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<DefaultModel> call, Throwable t) {
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
