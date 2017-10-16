package com.gmail.pasquarelli.brandon.destinyapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class ManifestDefinition {

    @SerializedName("version")
    String version;

    @SerializedName("mobileWorldContentPaths")
    HashMap<String, String> worldContentPaths;

    @SerializedName("mobileClanBannerDatabasePath")
    String clannBannerDatabasePath;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HashMap<String, String> getWorldContentPaths() {
        return worldContentPaths;
    }

    public void setWorldContentPaths(HashMap<String, String> worldContentPaths) {
        this.worldContentPaths = worldContentPaths;
    }

    public String getClannBannerDatabasePath() {
        return clannBannerDatabasePath;
    }

    public void setClannBannerDatabasePath(String clannBannerDatabasePath) {
        this.clannBannerDatabasePath = clannBannerDatabasePath;
    }
}
