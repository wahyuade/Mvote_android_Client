package client_android.m_vote.feature;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import client_android.m_vote.R;
import client_android.m_vote.feature.pilih_calon.CalonActivity;
import client_android.m_vote.model.LoginModel;
import client_android.m_vote.service.ApiService;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {
    Button login_button;
    EditText nrp, token;
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

                    ApiService.service_post.postLogin(RequestBody.create(MultipartBody.FORM, data_nrp), RequestBody.create(MultipartBody.FORM, data_token)).enqueue(new Callback<LoginModel>() {
                        @Override
                        public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                            if(response.isSuccessful()){
                                if(response.body().isSuccess()){
                                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent lihat_pilihan_calon = new Intent(LoginActivity.this, CalonActivity.class);
                                    lihat_pilihan_calon.putExtra("local", response.body().getLocal());
                                    LoginActivity.this.startActivity(lihat_pilihan_calon);
                                    LoginActivity.this.finish();
                                }else{
                                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(LoginActivity.this, Integer.toString(response.code()), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginModel> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
