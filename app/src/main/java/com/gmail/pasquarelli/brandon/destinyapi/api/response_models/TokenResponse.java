package com.gmail.pasquarelli.brandon.destinyapi.api.response_models;

import com.google.gson.annotations.SerializedName;

public class TokenResponse extends Response {

    @SerializedName("token_type")
    public String tokenType;

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("expires_in")
    public int accessTokenExpires;

    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("refresh_expires_in")
    public int refreshTokenExpires;

    @SerializedName("membership_id")
    public String bungieMembershipId;
}
