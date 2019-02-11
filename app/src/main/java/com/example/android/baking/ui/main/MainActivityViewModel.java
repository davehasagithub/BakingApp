package com.example.android.baking.ui.main;

import android.app.Application;
import android.content.Context;

import com.example.android.baking.data.repo.RecipeRepository;
import com.example.android.baking.data.struct.Recipe;
import com.example.android.baking.data.struct.RecipeLoadStatus;
import com.example.android.baking.utilities.Event;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

@SuppressWarnings("WeakerAccess")
public class MainActivityViewModel extends AndroidViewModel {

    boolean initialized = false;
    private Context context = getApplication().getApplicationContext();
    private LiveData<List<Recipe>> recipesDbLiveData;
    private MutableLiveData<Integer> recipeIdLiveData = new MutableLiveData<>();
    private MediatorLiveData<Recipe> recipeLiveData = new MediatorLiveData<>();
    private MutableLiveData<Integer> recipeIndexLiveData = new MutableLiveData<>();
    private MediatorLiveData<RecipeLoadStatus> statusLiveData = new MediatorLiveData<>();
    private MutableLiveData<List<Recipe>> placeholdersLiveData = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> handleRecipeClickLiveData = new MutableLiveData<>();

    private LiveData<List<Recipe>> recipesLiveData = Transformations.switchMap(statusLiveData, new Function<RecipeLoadStatus, LiveData<List<Recipe>>>() {
        @Override
        public LiveData<List<Recipe>> apply(RecipeLoadStatus input) {
            boolean isDataLoadedSuccessfully = RecipeLoadStatus.SUCCESS.equals(statusLiveData.getValue());
            return isDataLoadedSuccessfully ? recipesDbLiveData : placeholdersLiveData;
        }
    });

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(final int recipeId) {
        if (!initialized) {
            initialized = true;
            placeholdersLiveData.setValue(Recipe.constructPlaceholders(4));
            recipesDbLiveData = RecipeRepository.getInstance().loadRecipesLiveData(context);
            recipeIdLiveData.setValue(recipeId);
            updateRecipes(false);

            recipeLiveData.addSource(recipeIdLiveData, new Observer<Integer>() {
                @Override
                public void onChanged(Integer currentRecipeId) {
                    findRecipeInListAndAssign(recipesLiveData.getValue(), currentRecipeId);
                }
            });

            recipeLiveData.addSource(recipesLiveData, new Observer<List<Recipe>>() {
                @Override
                public void onChanged(List<Recipe> recipes) {
                    findRecipeInListAndAssign(recipes, recipeIdLiveData.getValue());
                }
            });
        }
    }

    public void findRecipeInListAndAssign(List<Recipe> recipes, Integer currentRecipeId) {
            Recipe returnRecipe = null;
            recipeIndexLiveData.setValue(-1);
            if (currentRecipeId != null && currentRecipeId != -1) {
                if (recipes != null) {
                    for (int i = 0; i < recipes.size(); i++) {
                        Recipe recipe = recipes.get(i);
                        if (recipe.getRecipeDb().getId() == currentRecipeId) {
                            returnRecipe = recipe;
                            recipeIndexLiveData.setValue(i);
                            break;
                        }
                    }
                }
            }
            recipeLiveData.setValue(returnRecipe);
    }

    public void updateRecipes(boolean forceReload) {
        if (!RecipeLoadStatus.LOADING.equals(statusLiveData.getValue())) {
            statusLiveData.setValue(RecipeLoadStatus.LOADING);
            final LiveData<Boolean> resultLiveData = RecipeRepository.getInstance().reloadRecipesIfNecessary(context, forceReload);
            statusLiveData.addSource(resultLiveData, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean success) {
                    statusLiveData.removeSource(resultLiveData);
                    statusLiveData.setValue(success ? RecipeLoadStatus.SUCCESS : RecipeLoadStatus.FAIL);
                }
            });
        }
    }

    public void triggerRecipeLoad(Recipe recipe) {
        recipeIdLiveData.setValue(recipe == null ? -1 : recipe.recipeDb.getId());
        handleRecipeClickLiveData.setValue(new Event<>(recipe != null));
    }

    public void clearRecipe() {
        recipeIdLiveData.setValue(-1);
    }

    public void onRetryButtonClick() {
        updateRecipes(true);
    }

    public LiveData<List<Recipe>> getRecipesLiveData() {
        return recipesLiveData;
    }

    public LiveData<Recipe> getRecipeLiveData() {
        return recipeLiveData;
    }

    public MutableLiveData<Event<Boolean>> getHandleRecipeClickLiveData() {
        return handleRecipeClickLiveData;
    }

    public int getRecipeId() {
        Integer currentRecipeId = recipeIdLiveData.getValue();
        return (currentRecipeId == null ? -1 : currentRecipeId);
    }

    public LiveData<RecipeLoadStatus> getStatusLiveData() {
        return statusLiveData;
    }

    public MutableLiveData<Integer> getRecipeIndexLiveData() {
        return recipeIndexLiveData;
    }
}
