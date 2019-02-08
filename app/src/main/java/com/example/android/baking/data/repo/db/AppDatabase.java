package com.example.android.baking.data.repo.db;

import android.content.Context;

import com.example.android.baking.data.struct.IngredientDb;
import com.example.android.baking.data.struct.RecipeDb;
import com.example.android.baking.data.struct.StepDb;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                RecipeDb.class,
                IngredientDb.class,
                StepDb.class
        },
        version = 1,
        exportSchema = false
)
// @TypeConverters(ModelAdapters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "baking";

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, AppDatabase.DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }

    public abstract RecipeDao recipeDao();
}
