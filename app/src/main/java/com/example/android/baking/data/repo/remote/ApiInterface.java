package com.example.android.baking.data.repo.remote;

import com.example.android.baking.data.struct.RecipeRemote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("/android-baking-app-json")
    Call<List<RecipeRemote>> getRecipes();

    @GET("/android-baking-app-json-BAD-FOR-TESTING")
    Call<List<RecipeRemote>> getRecipesBad();

}
