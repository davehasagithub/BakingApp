package com.example.android.baking.data.struct;

import com.squareup.moshi.Json;

import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class RecipeRemote {

    private int id;

    private String name;

    private List<IngredientRemote> ingredients;

    private List<StepRemote> steps;

    private int servings;

    @Json(name = "image")
    private String imageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IngredientRemote> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientRemote> ingredients) {
        this.ingredients = ingredients;
    }

    public List<StepRemote> getSteps() {
        return steps;
    }

    public void setSteps(List<StepRemote> steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
