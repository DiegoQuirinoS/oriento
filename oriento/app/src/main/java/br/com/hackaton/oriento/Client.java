package br.com.hackaton.oriento;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by FELIPE on 07/10/2017.
 */

public interface Client {

    @GET("group/{x}/{y}")
    Call<String> linhas(@Path("x") double x, @Path("y") double y);

    @GET("pontos")
    Call<String> linhas();


}
