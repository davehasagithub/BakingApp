package com.example.android.baking.utilities;

import android.content.Context;
import android.os.SystemClock;

import com.example.android.baking.R;
import com.example.android.baking.data.repo.remote.ApiInterface;
import com.squareup.moshi.Moshi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class WebService {

    private static WebService instance;

    private final ApiInterface apiInterface;

    private WebService(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    private static WebService getInstance(Context context) {
        if (instance == null) {
            synchronized (WebService.class) {
                if (instance == null) {
                    instance = new WebService(makeApiInterface(context));
                }
            }
        }
        return instance;
    }

    public static ApiInterface api(Context context) {
        return getInstance(context).apiInterface;
    }

    private static ApiInterface makeApiInterface(Context context) {
        return new Builder()
                .baseUrl(context.getString(R.string.recipe_service_domain))
                .addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder().build()))
                .callFactory(getHttpClient(false))
                .build()
                .create(ApiInterface.class);
    }

    // can get a version for testing that includes logging and a 2 second delay in each request
    private static OkHttpClient getHttpClient(@SuppressWarnings("SameParameterValue") boolean forTesting) {
        OkHttpClient httpClient;
        if (forTesting) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        SystemClock.sleep(2000);
                        return chain.proceed(chain.request());
                    })
                    .addInterceptor(logging)
                    .build();
        } else {
            httpClient = new OkHttpClient.Builder().build();
        }
        return httpClient;
    }
}