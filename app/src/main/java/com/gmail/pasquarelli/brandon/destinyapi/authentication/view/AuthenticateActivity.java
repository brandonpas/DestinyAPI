package com.gmail.pasquarelli.brandon.destinyapi.authentication.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.gmail.pasquarelli.brandon.destinyapi.R;
import com.gmail.pasquarelli.brandon.destinyapi.api.ApiUtility;
import com.gmail.pasquarelli.brandon.destinyapi.api.request_models.AuthRequestModel;
import com.gmail.pasquarelli.brandon.destinyapi.api.response_models.TokenResponse;
import com.gmail.pasquarelli.brandon.destinyapi.authentication.models.AuthHelper;
import com.gmail.pasquarelli.brandon.destinyapi.authentication.models.AuthWebViewClient;
import com.gmail.pasquarelli.brandon.destinyapi.authentication.viewmodel.AuthenticateViewModel;
import com.gmail.pasquarelli.brandon.destinyapi.publicmilestoneslist.view.WeeklyMilestonesActivity;

import java.util.Calendar;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AuthenticateActivity extends AppCompatActivity {
    private String TAG = "AuthActiv";
    private WebView webView;
    private AuthWebViewClient webViewClient;
    private AuthenticateViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        // If already have an active token, skip authentication
        if (haveAuthToken())
            launchNextScreen();
        else {
            initViewModel();
            openAuthPage();
        }
    }

    @Override
    public void onBackPressed() {
        if(webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void initViewModel() {
        authViewModel = ViewModelProviders.of(this).get(AuthenticateViewModel.class);
    }

    /**
     * Load the Bungie authentication web page
     */
    public void openAuthPage() {
        if (webView == null) {
            webView = findViewById(R.id.webview);
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);

            webViewClient = new AuthWebViewClient();
            setObserver(webViewClient);
            // Force links and redirects to open in the WebView instead of in a browser
            webView.setWebViewClient(webViewClient);
        }

        webView.loadUrl(AuthHelper.getAuthUrl().toString());
    }

    /**
     * When the authorization code is received, we need to get the access token and refresh token
     * in the background. We do not need to display any of these steps to the user, so the webview
     * does not need to load anything
     * @param client The AuthWebViewClient we are observing
     */
    void setObserver(AuthWebViewClient client) {
        client.getBungieAuthCode().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String authCode) {
                // we've received the token
                Log.v(TAG, "auth code: " +  authCode);
                if (authCode.length() > 0) {
                    ApiUtility.getDebugService()
                            .getAccessTokenConfidential(new AuthRequestModel(authCode).getBody())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(getTokenObserver());
                }
            }
        });
    }

    /**
     * Set up and return the observer for when we receive the token
     * @return
     */
    SingleObserver<TokenResponse> getTokenObserver() {
        final String TOKEN_TAG="AccessTokenAPI";
        return new SingleObserver<TokenResponse>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) { Log.v(TOKEN_TAG, "onSubscribe"); }

            @Override
            public void onSuccess(@NonNull TokenResponse tokenResponse) {
                Log.v(TOKEN_TAG, "onSuccess");
                if (tokenResponse.refreshToken != null  && tokenResponse.refreshToken.length() > 0)
                    tokenSuccess(tokenResponse);
                else
                    tokenError("Token is null");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.v(TOKEN_TAG, "onError");
                tokenError(e.getMessage());
                e.printStackTrace();
            }
        };
    }

    /**
     * Called when we receive a valid token for the user. Store the token for later use,
     * and proceed into the application
     * @param response The response object from the oauth API call.
     */
    private void tokenSuccess(TokenResponse response) {
        Log.v(TAG, "tokenSuccess: " + response.refreshToken);
        if (webView != null)
            webView.stopLoading();
        showToast("Success");
        storeToken(response.refreshToken, response.refreshTokenExpires);
        launchNextScreen();
    }

    /**
     * Called when the oauth API call errors or if we do not receive a token for some reason.
     * @param errorMessage Some message indicating why the token API failed
     */
    private void tokenError(String errorMessage) {
        Log.v(TAG, "tokenError");
        showToast("Unable to authenticate");
        openAuthPage();
    }

    /**
     * Store the auth token in SharedPreferences for now. Only our app can access this file, however
     * on rooted devices the user could read this file. We'll need to update this logic to encrypt the
     * token.
     * @param token Token to store
     * @param hoursValid Integer representing the number of hours the token is valid
     */
    private void storeToken(String token, int hoursValid) {
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.HOUR, hoursValid);

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.refresh_token), token);
        editor.putLong(getString(R.string.refresh_token_expiration), endDate.getTimeInMillis());
        editor.apply();
    }

    /**
     * Display a message to the user
     * @param message Message to display
     */
    private void showToast(String message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    /**
     * Display a message to the user
     * @param message Message to show
     * @param duration Toast.LENGTH_LONG or Toast.LENGTH_SHORT
     */
    private void showToast(String message, int duration) {
        if (duration != 1 && duration != 0)
            duration = Toast.LENGTH_LONG;

        if (message == null || message.length() == 0)
            return;

        Toast.makeText(this, message, duration).show();
    }

    /**
     * Verify that we have a token that has not expired.
     * @return True if valid, otherwise false.
     */
    private boolean haveAuthToken() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String token = prefs.getString(getString(R.string.refresh_token), null);
        long expiration = prefs.getLong(getString(R.string.refresh_token_expiration), 0L);
        if (token != null && expiration != 0L) {
            // we have a token, check the expiration
            Calendar currentDate = Calendar.getInstance();
            return currentDate.getTimeInMillis() < expiration;
        } else {
            return false;
        }
    }

    /**
     * Launch the next screen after verifying/obtaining an authorization token
     */
    private void launchNextScreen() {
        Intent milestonesList = WeeklyMilestonesActivity.getIntent(this);
        startActivity(milestonesList);
        finish();
    }

    /**
     * Retrieve an intent to launch the AuthenticateActivity
     * @param context Context for the current screen.
     * @return Intent
     */
    public static Intent getIntent(Context context) {
        return new Intent(context, AuthenticateActivity.class);
    }
}
