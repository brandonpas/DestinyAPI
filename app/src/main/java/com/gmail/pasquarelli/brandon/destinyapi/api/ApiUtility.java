package com.gmail.pasquarelli.brandon.destinyapi.api;

import com.gmail.pasquarelli.brandon.destinyapi.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtility {

    private static String BASE_URL = "https://bungie.net/Platform/";
    private static Retrofit client;

    /**
     * Private constructor for utility type classes
     */
    private ApiUtility() { }

    /**
     * Obtain a standard Retrofit client.
     * @return A standard Retrofit instance with {@link RxJava2CallAdapterFactory}
     * and {@link GsonConverterFactory} pre-set.
     */
    public static Retrofit getClient() {
        if (client == null) {
            client = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return client;
    }

    /**
     * Obtain the ApiService for all API class.
     * @return A standard API service.
     */
    public static ApiService getService() {
        return getClient().create(ApiService.class);
    }

}
