package com.gmail.pasquarelli.brandon.destinyapi.weaponstats.model;

import android.support.annotation.NonNull;

import com.gmail.pasquarelli.brandon.destinyapi.utils.Conversions;

public class SocketFilterItem {

    private String unsignedIntHash;
    private int signedIntHash;
    private String perkName;
    private String perkDescription;

    public SocketFilterItem(@NonNull String hash) {
        unsignedIntHash = hash;
        signedIntHash = Conversions.parseUnsignedInt(unsignedIntHash);
    }

    public String getUnsignedIntHash() {
        return unsignedIntHash;
    }

    public void setUnsignedIntHash(String unsignedIntHash) { this.unsignedIntHash = unsignedIntHash; }

    public String getPerkName() {
        return perkName;
    }

    public void setPerkName(String perkName) {
        this.perkName = perkName;
    }

    public int getSignedIntHash() { return signedIntHash; }

    public String getPerkDescription() {
        return perkDescription;
    }

    public void setPerkDescription(String perkDescription) {
        this.perkDescription = perkDescription;
    }
}
