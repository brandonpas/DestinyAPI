package com.gmail.pasquarelli.brandon.destinyapi.api;

import android.util.Log;

import com.gmail.pasquarelli.brandon.destinyapi.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Utility class for the Retrofit client
 */
public class ApiUtility {

    private static String TAG = "ApiUtil";
    private static String BASE_URL = "https://www.bungie.net/Platform/";
    private static String BUNGIE_BASE_URL = "https://www.bungie.net";

    private static Retrofit client;
    private static Retrofit debugClient = null;

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

    static Retrofit getDebugClient() {
        if (BuildConfig.DEBUG) {
            if (debugClient == null) {
                OkHttpClient.Builder httpClient;
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.v(TAG, message);
                    }
                });
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(logging);
                debugClient = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient.build())
                        .build();
            }
            return debugClient;
        } else {
            return getClient();
        }
    }

    /**
     * Obtain the ApiService for all API class.
     * @return A standard API service.
     */
    public static ApiService getService() {
        return getClient().create(ApiService.class);
    }

    public static ApiService getDebugService() {
        return getDebugClient().create(ApiService.class);
    }

    /**
     * Obtain the ApiService for all API class.
     * @return A standard API service.
     */
    public static ApiService getBungieService() {
        return getBungieClient().create(ApiService.class);
    }

}
