package com.gmail.pasquarelli.brandon.destinyapi.api.response_models;


import com.gmail.pasquarelli.brandon.destinyapi.model.ManifestDefinition;
import com.google.gson.annotations.SerializedName;

public class ManifestResponse extends Response {

    @SerializedName("Response")
    public ManifestDefinition manifest;

    public ManifestDefinition getManifest() {
        return manifest;
    }

    public void setManifest(ManifestDefinition manifest) {
        this.manifest = manifest;
    }
}
