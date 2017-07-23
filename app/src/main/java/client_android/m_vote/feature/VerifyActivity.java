package client_android.m_vote.feature;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import client_android.m_vote.R;
import client_android.m_vote.model.VerifyModel;
import client_android.m_vote.service.ApiServiceTTP;
import client_android.m_vote.service.SqliteDatabaseService;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyActivity extends Activity {
    Button verify;
    EditText code_verification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        verify = (Button)findViewById(R.id.verify);
        code_verification = (EditText)findViewById(R.id.code_verification);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kode_verifikasi = code_verification.getText().toString();
                if(!kode_verifikasi.isEmpty()){
                    ApiServiceTTP.service_post.postVerify(RequestBody.create(MultipartBody.FORM, kode_verifikasi)).enqueue(new Callback<VerifyModel>() {
                        @Override
                        public void onResponse(Call<VerifyModel> call, Response<VerifyModel> response) {
                            if(response.isSuccessful()){
                                if(response.body().isSuccess()){
                                    Toast.makeText(VerifyActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    SqliteDatabaseService db = new SqliteDatabaseService(VerifyActivity.this);
                                    if(db.verifyDpt(response.body())){
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
                    Toast.makeText(VerifyActivity.this, "Mohon masukkan kode verifikasi Anda", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
