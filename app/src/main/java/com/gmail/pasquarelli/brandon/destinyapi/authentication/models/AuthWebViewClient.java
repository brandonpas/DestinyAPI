package com.gmail.pasquarelli.brandon.destinyapi.authentication.models;

import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class AuthWebViewClient extends WebViewClient {
    private String TAG = "AuthWebView";

    MutableLiveData<String> authCode;

    public MutableLiveData<String> getBungieAuthCode() {
        if (authCode == null)
            authCode = new MutableLiveData<>();
        return authCode;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.v(TAG,url);
        if (url != null && url.contains("https://localhost:8080/?code=")) {
            // prevent this page from being displayed since it isn't a real page.
            view.stopLoading();
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        // If Bungie/PSN/Xbox, then false. Else true
        // Bungie : www.bungie.net
        // PSN : auth.api.sonyentertainmentnetwork.com
        // Xbox :
        // Battle.net?
        Log.v(TAG,"Url: " + url);
        if (url != null && url.contains("https://localhost:8080/?code=")) {
            //we've been granted access
            Uri urlReceived = Uri.parse(url);
            String code = urlReceived.getQueryParameter("code");
            Log.v(TAG,"Token from bungie override: " + code);
            authCode.setValue(code);
        }
        return false;
    }
}
