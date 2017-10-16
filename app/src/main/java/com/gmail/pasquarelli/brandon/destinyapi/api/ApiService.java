package com.gmail.pasquarelli.brandon.destinyapi.api;

import com.gmail.pasquarelli.brandon.destinyapi.BuildConfig;
import com.gmail.pasquarelli.brandon.destinyapi.api.response_models.ManifestResponse;
import com.gmail.pasquarelli.brandon.destinyapi.api.response_models.PublicMilestonesResponse;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;

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
}
