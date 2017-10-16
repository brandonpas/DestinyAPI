package com.gmail.pasquarelli.brandon.destinyapi.api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Utility class for the Retrofit client
 */
public class ApiUtility {

    private static String BASE_URL = "https://bungie.net/Platform/";
    private static String BUNGIE_BASE_URL = "https://bungie.net";
    private static Retrofit client;

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getBungieBaseUrl() {
        return BUNGIE_BASE_URL;
    }

    /**
     * Private constructor for utility type classes
     */
    private ApiUtility() { }

    /**
     * Obtain a standard Retrofit client.
     * @return A standard Retrofit instance with {@link RxJava2CallAdapterFactory}
     * and {@link GsonConverterFactory} pre-set.
     */
    static Retrofit getClient() {
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
     * Obtain a standard Retrofit client.
     * @return A standard Retrofit instance with {@link RxJava2CallAdapterFactory}
     * and {@link GsonConverterFactory} pre-set.
     */
    static Retrofit getBungieClient() {
        if (client == null) {
            client = new Retrofit.Builder()
                    .baseUrl(BUNGIE_BASE_URL)
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

    /**
     * Obtain the ApiService for all API class.
     * @return A standard API service.
     */
    public static ApiService getBungieService() {
        return getBungieClient().create(ApiService.class);
    }

}
