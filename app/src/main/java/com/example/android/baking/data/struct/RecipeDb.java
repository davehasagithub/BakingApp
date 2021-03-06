package com.example.android.baking.data.struct;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "recipe"
)
@SuppressWarnings({"unused", "WeakerAccess"})
public class RecipeDb {

    @PrimaryKey
    private int id;

    private String name;

    private int servings;

    private String imageUrl;

    public RecipeDb(int id, String name, int servings, String imageUrl) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.imageUrl = imageUrl;
    }

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

    public RecipeDb(RecipeRemote recipe) {
        this.id = recipe.getId();
        this.name = recipe.getName();
        this.servings = recipe.getServings();
        this.imageUrl = recipe.getImageUrl();
    }

    /**
     * @see Recipe#constructPlaceholders(int)
     */
    public boolean isPlaceholder() {
        return getId() < 0;
    }
}
