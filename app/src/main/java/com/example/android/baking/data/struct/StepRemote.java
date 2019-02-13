package com.example.android.baking.data.struct;

import com.squareup.moshi.Json;

@SuppressWarnings({"unused", "WeakerAccess"})
class StepRemote {

    // ignore json name = "id", use autoGenerate id in StepDb

    private String shortDescription;

    private String description;

    @Json(name = "videoURL")
    private String videoUrl;

    @Json(name = "thumbnailURL")
    private String thumbnailUrl;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
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
}
