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

    private final String BUNGIE_AUTHORITY = "www.bungie.net";
    private final String PSN_AUTHORITY = "auth.api.sonyentertainmentnetwork.com";
    private final String XBOX_AUTHORITY = "login.live.com";
    private final String BATTLENET_AUTHORITY = "us.battle.net";
    private final String REDIRECT_AUTHORITY = "localhost:8080";

    /**
     * An object for the Activity to observe and react when we receive an
     * authorization code
     * @return {@link MutableLiveData} object of the authorization code
     */
    public MutableLiveData<String> getBungieAuthCode() {
        if (authCode == null)
            authCode = new MutableLiveData<>();
        return authCode;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.v(TAG,url);
        if (url != null && url.contains(REDIRECT_AUTHORITY)) {
            // prevent this page from being displayed since it isn't a real page.
            view.stopLoading();
        }
    }

    /**
     * Check if this app's WebView should handle a redirect URL.
     *
     * To mitigate security concerns, this in app browser should only allow limited access to the
     * PSN, Xbox, Battle.net, or Bungie authentication pages. However, these authentication pages
     * could also use other authentication services (Facebook, Google, etc), so we can't restrict
     * the WebView to only load pages for the four authorities above.
     *
     * @param view
     * @param url
     * @return
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url == null || (url.length() == 0))
            return false;

        Log.v(TAG,"Url: " + url);
        Uri uri = Uri.parse(url);
        if (uri.getAuthority().equals(REDIRECT_AUTHORITY))
            authCode.setValue(uri.getQueryParameter("code"));
        return false;
    }

}
