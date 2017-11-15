package com.gmail.pasquarelli.brandon.destinyapi.authentication.models;

import android.net.Uri;

import com.gmail.pasquarelli.brandon.destinyapi.BuildConfig;

public class AuthHelper {

    public static Uri getAuthUrl() {
        return new Uri.Builder()
                .scheme("https")
                .authority("www.bungie.net")
                .appendPath("en")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", BuildConfig.destinyOauthClient)
                .appendQueryParameter("response_type","code")
                .build();

    }

}
