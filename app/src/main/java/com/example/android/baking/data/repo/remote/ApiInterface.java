package com.example.android.baking.data.repo.remote;

import com.example.android.baking.data.struct.RecipeRemote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET
        // path comes from R.string.recipe_service_endpoint
        // found out about this here: https://stackoverflow.com/a/32559579
    Call<List<RecipeRemote>> getRecipes(@Url String url);

}
