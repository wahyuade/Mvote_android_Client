package client_android.m_vote.service;

import java.util.ArrayList;

import client_android.m_vote.model.CalonModel;
import client_android.m_vote.model.DefaultModel;
import client_android.m_vote.model.LoginModel;
import client_android.m_vote.model.VerifyModel;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by wahyuade on 16/07/17.
 */

public class ApiServiceTTP {
    public static String BASE_URL = "http://192.168.43.10:8080";

    public static PostService service_post = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build().create(ApiServiceTTP.PostService.class);

    public static GetService service_get = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build().create(ApiServiceTTP.GetService.class);

    public interface GetService{

    }

    public interface PostService{
        @Multipart
        @POST("/verify")
        Call<VerifyModel> postVerify(@Part("verify_code") RequestBody verify_code);
    }
}
