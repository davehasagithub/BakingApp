package com.example.android.baking.data.repo.db;

import com.example.android.baking.data.struct.IngredientDb;
import com.example.android.baking.data.struct.RecipeDb;
import com.example.android.baking.data.struct.Recipe;
import com.example.android.baking.data.struct.StepDb;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public abstract class RecipeDao {

    @Query("delete from recipe")
    public abstract void deleteAllRecipes();

    @Query("delete from ingredient")
    public abstract void deleteAllIngredients();

    @Query("delete from step")
    public abstract void deleteAllSteps();

    @Transaction
    public void deleteAll() {
        deleteAllRecipes();
        deleteAllIngredients();
        deleteAllSteps();
    }

    @Insert
    public abstract void insertRecipe(RecipeDb recipe);

    @Insert
    public abstract void insertIngredients(List<IngredientDb> ingredients);

    @Insert
    public abstract void insertSteps(List<StepDb> steps);

    @Transaction
    // https://stackoverflow.com/a/47112918
    public void saveRecipesWithIngredientsAndSteps(List<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            List<IngredientDb> ingredients = recipe.getIngredients();
            for (IngredientDb ingredient : ingredients) {
                ingredient.setRecipeId(recipe.recipeDb.getId());
            }

            List<StepDb> steps = recipe.getSteps();
            for (StepDb step : steps) {
                step.setRecipeId(recipe.recipeDb.getId());
            }

            insertIngredients(ingredients);
            insertSteps(steps);
            insertRecipe(recipe.recipeDb);
        }
    }

    @Transaction
    @Query("SELECT * FROM recipe ORDER BY id")
    public abstract LiveData<List<Recipe>> getRecipesLiveData();

    @Transaction
    @Query("SELECT * FROM recipe WHERE id = :id")
    public abstract Recipe getRecipe(int id);
}
