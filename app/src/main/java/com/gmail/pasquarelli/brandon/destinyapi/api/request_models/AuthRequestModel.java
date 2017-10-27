package com.gmail.pasquarelli.brandon.destinyapi.api.request_models;

import android.support.annotation.NonNull;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AuthRequestModel {


    private String authCode;

    public AuthRequestModel(@NonNull String code) {
        authCode = code;
    }

    public RequestBody getBody() {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        return RequestBody.create(mediaType, getContent());
    }

    private String getContent() {
        return getGrantType() + "&" + getAuthCode();
    }

    private String getGrantType() {
        return "grant_type=authorization_code";
    }

    private String getAuthCode() {
        return "code=" + authCode;
    }
}
