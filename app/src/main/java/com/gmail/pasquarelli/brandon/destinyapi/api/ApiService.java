package com.gmail.pasquarelli.brandon.destinyapi.api;

import com.gmail.pasquarelli.brandon.destinyapi.BuildConfig;
import com.gmail.pasquarelli.brandon.destinyapi.data.GetPublicMilestonesResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ApiService {

    /**
     * Destiny2.GetPublicMilestones
     * <p>
     * Gets public information about currently available Milestones.
     * @return An Observable object representing the API response.
     */
    @Headers("X-API-Key: " + BuildConfig.destiny2ApiKey)
    @GET("Destiny2/Milestones/")
    Observable<GetPublicMilestonesResponse> getPublicMilestones();

}
