package client_android.m_vote.feature;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.SecureRandom;

import client_android.m_vote.R;
import client_android.m_vote.feature.pilih_calon.CalonActivity;
import client_android.m_vote.model.LoginModel;
import client_android.m_vote.service.ApiServiceAdmin;
import client_android.m_vote.service.SqliteDatabaseService;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {
    Button login_button;
    EditText nrp, token;
    BigInteger r;
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
                                            Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            db.saveRandGenerateX(String.valueOf(r));
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
}
