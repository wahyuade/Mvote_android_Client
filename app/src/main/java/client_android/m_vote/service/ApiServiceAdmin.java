package client_android.m_vote.service;

import java.util.ArrayList;

import client_android.m_vote.model.CalonModel;
import client_android.m_vote.model.ChallangeModel;
import client_android.m_vote.model.DefaultModel;
import client_android.m_vote.model.LoginModel;
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

public class ApiServiceAdmin {
    public static String BASE_URL = "http://mvotepenscom-over.cloud.revoluz.io:49528";
    public static String SOCKET_URL = "ws://mvotepenscom-over.cloud.revoluz.io:49528";

    public static PostService service_post = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build().create(ApiServiceAdmin.PostService.class);

    public static GetService service_get = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build().create(ApiServiceAdmin.GetService.class);

    public interface GetService{
        @GET("/list_calon")
        Call<ArrayList<CalonModel>> getListCalon(@Query("value_n") String value_n);
    }

    public interface PostService{
        @Multipart
        @POST("/login")
        Call<LoginModel> postLogin(@Part("nrp") RequestBody nrp, @Part("token") RequestBody token);

        @Multipart
        @POST("/check_m")
        Call<ChallangeModel> postCheck_m(@Part("value_n") RequestBody value_n, @Part("x") RequestBody x, @Part("h") RequestBody h);

        @Multipart
        @POST("/vote_validate")
        Call<DefaultModel> postVoteValidate(@Part("nrp") RequestBody nrp, @Part("y_value") RequestBody y, @Part("h_value") RequestBody h, @Part("c_value") RequestBody c);
    }
}
