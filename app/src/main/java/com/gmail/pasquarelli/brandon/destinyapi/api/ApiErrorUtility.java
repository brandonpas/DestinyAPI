package com.gmail.pasquarelli.brandon.destinyapi.api;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public final class ApiErrorUtility {

    private ApiErrorUtility() { }

    /**
     * Converts the Retrofit {@link Response} to a more friendly
     * {@link ApiError} object.
     * @param response Retrofit {@code Response} object
     * @return An {@code ApiError} object
     */
    public static ApiError parseError(Response<?> response) {

        Converter<ResponseBody, ApiError> converter
                = ApiUtility.getClient().responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError error;
        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ApiError();
        }

        return error;
    }
}
