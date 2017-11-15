package com.gmail.pasquarelli.brandon.destinyapi.api;

import android.os.Build;

import com.gmail.pasquarelli.brandon.destinyapi.BuildConfig;
import com.gmail.pasquarelli.brandon.destinyapi.api.request_models.AuthRequestModel;
import com.gmail.pasquarelli.brandon.destinyapi.api.response_models.ManifestResponse;
import com.gmail.pasquarelli.brandon.destinyapi.api.response_models.PublicMilestonesResponse;
import com.gmail.pasquarelli.brandon.destinyapi.api.response_models.TokenResponse;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    /**
     * Destiny2.GetPublicMilestones
     * <p>
     * Asynchronously retrieves information about currently available Milestones.
     * @return An Observable object representing the API response.
     */
    @Headers("X-API-Key: " + BuildConfig.destiny2ApiKey)
    @GET("Destiny2/Milestones/")
    Observable<PublicMilestonesResponse> getPublicMilestones();

    /**
     * Destiny2.Manifest
     * <p>
     * Asynchronously retrieves the current Manifest information
     * @return An Observable object representing the API response.
     */
    @Headers("X-API-Key: " + BuildConfig.destiny2ApiKey)
    @GET("Destiny2/Manifest/")
    Single<ManifestResponse> getManifest();

    @Headers({"X-API-Key: " + BuildConfig.destiny2ApiKey,
            "Content-Type: application/x-www-form-urlencoded",
            "Authorization: Basic " + BuildConfig.oauthAuthHeader,
            "cache-control: no-cache"})
    @POST("App/OAuth/Token/")
    Single<TokenResponse> getAccessTokenPublic(
            @Body RequestBody content);


    @Headers({"X-API-Key: " + BuildConfig.destiny2ApiKey,
    "Content-Type: application/x-www-form-urlencoded",
    "Authorization: Basic " + BuildConfig.oauthAuthHeader,
    "cache-control: no-cache"})
    @POST("App/OAuth/Token/")
    Single<TokenResponse> getAccessTokenConfidential(
            @Body RequestBody content);
}
