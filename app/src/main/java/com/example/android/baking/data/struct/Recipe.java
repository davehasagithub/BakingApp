package com.example.android.baking.data.struct;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class Recipe implements Parcelable {

    @Embedded
    public RecipeDb recipeDb;

    @Relation(parentColumn = "id", entityColumn = "recipeId", entity = IngredientDb.class)
    private List<IngredientDb> ingredients;

    @Relation(parentColumn = "id", entityColumn = "recipeId", entity = StepDb.class)
    private List<StepDb> steps;

    public Recipe(RecipeDb recipeDb) {
        this.recipeDb = recipeDb;
    }

    protected Recipe(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

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

    public Recipe(RecipeRemote recipe) {
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

    public static List<Recipe> constructPlaceholders() {
        int placeHolderCount = 6;
        List<Recipe> placeholders = new ArrayList<>();
        for (int i = -placeHolderCount; i < 0; i++) {
            RecipeDb recipeDb = new RecipeDb(i, "", 0, null);
            Recipe recipe = new Recipe(recipeDb);
            placeholders.add(recipe);
        }
        return placeholders;
    }
}
