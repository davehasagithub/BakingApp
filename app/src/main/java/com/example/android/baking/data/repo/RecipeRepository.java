package com.example.android.baking.data.repo;

import android.content.Context;
import android.preference.PreferenceManager;

import com.example.android.baking.BakingWidget;
import com.example.android.baking.R;
import com.example.android.baking.data.repo.db.AppDatabase;
import com.example.android.baking.data.struct.Recipe;
import com.example.android.baking.data.struct.RecipeRemote;
import com.example.android.baking.utilities.AppExecutors;
import com.example.android.baking.utilities.EspressoIdlingResource;
import com.example.android.baking.utilities.WebService;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class RecipeRepository {

    private final AtomicInteger counter = new AtomicInteger();

    private RecipeRepository() {
    }

    private static class SingletonHelper {
        private static final RecipeRepository INSTANCE = new RecipeRepository();
    }

    public static RecipeRepository getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public LiveData<List<Recipe>> loadRecipesLiveData(Context context) {
        return AppDatabase.getInstance(context).recipeDao().getRecipesLiveData();
    }

    public Recipe loadRecipe(Context context, int id) {
        return AppDatabase.getInstance(context).recipeDao().getRecipe(id);
    }

    public MutableLiveData<Boolean> reloadRecipesIfNecessary(final Context context, boolean forceReload) {
        final MutableLiveData<Boolean> successLiveData = new MutableLiveData<>();
        long lastDatabaseRefresh = PreferenceManager.getDefaultSharedPreferences(context).getLong("lastDatabaseRefresh", 0);
        long expiryTime = TimeUnit.MILLISECONDS.convert(context.getResources().getInteger(R.integer.expirationMinutes), TimeUnit.MINUTES);
        Timber.d("result: expiry time %d, since now: %d", expiryTime, System.currentTimeMillis() - lastDatabaseRefresh);
        boolean isStale = System.currentTimeMillis() - lastDatabaseRefresh > expiryTime;
        if (!EspressoIdlingResource.isInTest() && (forceReload || isStale)) {
            AppExecutors.getInstance().diskIO().execute(() -> {
                AppDatabase.getInstance(context).recipeDao().deleteAll();

                AppExecutors.getInstance().mainThread().execute(() -> {
                    Call<List<RecipeRemote>> callGood = WebService.api().getRecipes();
                    Call<List<RecipeRemote>> callBad = WebService.api().getRecipesBad();
                    int count = counter.incrementAndGet();
                    Call<List<RecipeRemote>> call = (count < -1) ? callBad : callGood;
                    call.enqueue(new Callback<List<RecipeRemote>>() {
                        @Override
                        public void onResponse(@NotNull Call<List<RecipeRemote>> call, @NotNull Response<List<RecipeRemote>> response) {
                            Timber.d("result: success:%s code:%d", response.isSuccessful(), response.code());

                            final List<RecipeRemote> remoteRecipes = response.body();
                            if (remoteRecipes != null && remoteRecipes.size() > 0) {

                                final List<Recipe> recipes = Recipe.getRecipeList(remoteRecipes);

                                AppExecutors.getInstance().diskIO().execute(() -> {
                                    AppDatabase.getInstance(context).recipeDao().saveRecipesWithIngredientsAndSteps(recipes);
                                    PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("lastDatabaseRefresh", System.currentTimeMillis()).apply();
                                    successLiveData.postValue(true);

                                    BakingWidget.refresh(context);
                                });
                            } else {
                                successLiveData.postValue(false);
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<List<RecipeRemote>> call, @NotNull Throwable t) {
                            Timber.e(t);
                            successLiveData.postValue(false);
                        }
                    });

                });
            });
        } else {
            successLiveData.setValue(true);
        }
        return successLiveData;
    }
}
