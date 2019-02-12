package com.example.android.baking.data.struct;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;

import com.example.android.baking.R;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "ingredient"
)
@SuppressWarnings({"unused", "WeakerAccess"})
public class IngredientDb {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int recipeId;

    private String quantity;

    private String measure;

    private String ingredient;

    public IngredientDb(int id, int recipeId, String quantity, String measure, String ingredient) {
        this.id = id;
        this.recipeId = recipeId;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public IngredientDb(int recipeId, IngredientRemote ingredient) {
        this.recipeId = recipeId;
        this.quantity = ""+ingredient.getQuantity();
        this.measure = ingredient.getMeasure();
        this.ingredient = ingredient.getIngredient();
    }

    public static List<IngredientDb> getIngredientList(int recipeId, List<IngredientRemote> ingredientRemotes) {
        List<IngredientDb> ingredientDbs = new ArrayList<>();
        for (IngredientRemote ingredient : ingredientRemotes) {
            ingredientDbs.add(new IngredientDb(recipeId, ingredient));
        }
        return ingredientDbs;
    }

    //

    public Spannable getCombinedAndCleanedIngredientDescription(Context context) {
        StringBuilder s = new StringBuilder();
        s.append(quantity.replaceAll("\\.0$", ""));
        s.append(" ");
        if (!measure.equalsIgnoreCase("unit")) {
            s.append(measure.toUpperCase());
            s.append(" ");
        }

        s.append(ingredient.toUpperCase()
                .replaceAll("(\\S)\\((\\S)", "$1 ($2")
                .replaceAll("(\\S),(\\S)", "$1, $2"));

        SpannableString result = new SpannableString(s);
        int indent = (int) context.getResources().getDimension(R.dimen.master_padding);
        result.setSpan(new LeadingMarginSpan.Standard(0, indent), 0, s.length(), 0);

        return result;
    }
}
