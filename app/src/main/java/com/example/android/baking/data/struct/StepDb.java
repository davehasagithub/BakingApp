package com.example.android.baking.data.struct;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "step"
)
public class StepDb {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int recipeId;

    private String shortDescription;

    private String description;

    private String videoUrl;

    private String thumbnailUrl;

    public StepDb(int id, int recipeId, String shortDescription, String description, String videoUrl, String thumbnailUrl) {
        this.id = id;
        this.recipeId = recipeId;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
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

    public String getShortDescription() {
        return StepDb.removeEndingPeriod(shortDescription);
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return StepDb.fixTemperatureSymbolMojibake(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public StepDb(int recipeId, StepRemote step) {
        this.recipeId = recipeId;
        this.shortDescription = step.getShortDescription();
        this.description = step.getDescription();
        this.videoUrl = step.getVideoUrl();
        this.thumbnailUrl = step.getThumbnailUrl();
    }

    public static List<StepDb> getStepList(int recipeId, List<StepRemote> stepRemotes) {
        List<StepDb> stepDbs = new ArrayList<>();
        for (StepRemote stepRemote : stepRemotes) {
            stepDbs.add(new StepDb(recipeId, stepRemote));
        }
        return stepDbs;
    }

    private static String fixTemperatureSymbolMojibake(String s) {
        return s == null ? null : s.replaceAll("(\\s)(\\d+)�([FC])(\\W)", "$1$2°$3$4");
    }

    private static String removeEndingPeriod(String s) {
        return s == null ? null : s.replaceAll("\\.+$", "");
    }
}
