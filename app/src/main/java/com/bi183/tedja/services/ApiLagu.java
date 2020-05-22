package com.bi183.tedja.services;

import com.bi183.tedja.model.ResponseData;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiLagu {
    @Multipart
    @POST("insert.php")
    Call<ResponseData> addData(
            @Part("judul_lagu") RequestBody judul_lagu,
            @Part("album_lagu") RequestBody album_lagu,
            @Part("artis") RequestBody artis,
            @Part("tahun") RequestBody tahun,
            @Part("negara") RequestBody negara,
            @Part("publisher") RequestBody publisher,
            @Part("genre") RequestBody genre,
            @Part MultipartBody.Part cover
    );

    @Multipart
    @POST("update.php")
    Call<ResponseData> updateData(
            @Part("id") RequestBody id,
            @Part("judul_lagu") RequestBody judul_lagu,
            @Part("album_lagu") RequestBody album_lagu,
            @Part("artis") RequestBody artis,
            @Part("tahun") RequestBody tahun,
            @Part("negara") RequestBody negara,
            @Part("publisher") RequestBody publisher,
            @Part("genre") RequestBody genre,
            @Part MultipartBody.Part cover
    );

    @GET("getdata.php")
    Call<ResponseData> getData();
}
