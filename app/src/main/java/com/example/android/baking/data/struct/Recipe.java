package com.example.android.baking.data.struct;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Recipe {

    @Embedded
    public RecipeDb recipeDb;

    @Relation(parentColumn = "id", entityColumn = "recipeId", entity = IngredientDb.class)
    private List<IngredientDb> ingredients;

    @Relation(parentColumn = "id", entityColumn = "recipeId", entity = StepDb.class)
    private List<StepDb> steps;

    @SuppressWarnings("WeakerAccess")
    public Recipe(RecipeDb recipeDb) {
        this.recipeDb = recipeDb;
    }

    public RecipeDb getRecipeDb() {
        return recipeDb;
    }

    public void setRecipeDb(RecipeDb recipeDb) {
        this.recipeDb = recipeDb;
    }

    public List<IngredientDb> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientDb> ingredients) {
        this.ingredients = ingredients;
    }

    public List<StepDb> getSteps() {
        return steps;
    }

    public void setSteps(List<StepDb> steps) {
        this.steps = steps;
    }

    private Recipe(RecipeRemote recipe) {
        this.recipeDb = new RecipeDb(recipe);
        this.ingredients = IngredientDb.getIngredientList(recipe.getId(), recipe.getIngredients());
        this.steps = StepDb.getStepList(recipe.getId(), recipe.getSteps());
    }

    public static List<Recipe> getRecipeList(List<RecipeRemote> recipeRemotes) {
        List<Recipe> recipeDbs = new ArrayList<>();
        for (RecipeRemote recipe : recipeRemotes) {
            recipeDbs.add(new Recipe(recipe));
        }
        return recipeDbs;
    }

    public static List<Recipe> constructPlaceholders(int placeHolderCount) {
        List<Recipe> placeholders = new ArrayList<>();
        for (int i = -placeHolderCount; i < 0; i++) {
            RecipeDb recipeDb = new RecipeDb(i, "", 0, null);
            Recipe recipe = new Recipe(recipeDb);
            placeholders.add(recipe);
        }
        return placeholders;
    }
}
