package com.example.android.baking.utilities;

import android.os.SystemClock;

import com.example.android.baking.data.repo.remote.ApiInterface;
import com.squareup.moshi.Moshi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class WebService {

    private final ApiInterface apiInterface;

    private WebService(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    private static class SingletonHelper {
        private static final WebService INSTANCE = new WebService(makeApiInterface());
    }

    public static ApiInterface api() {
        return SingletonHelper.INSTANCE.apiInterface;
    }

    private static ApiInterface makeApiInterface() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    SystemClock.sleep(2000);
                    return chain.proceed(chain.request());
                })
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl("http://go.udacity.com/")
                .addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder().build())) // .add(new ModelAdapters())
                .callFactory(httpClient)
                .build()
                .create(ApiInterface.class);
    }
}