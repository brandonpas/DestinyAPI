package com.gmail.pasquarelli.brandon.destinyapi.data;

import com.google.gson.annotations.SerializedName;

/**
 * Base class for API responses. Only contains properties
 * that can be included in any response.
 */
class Response {

    @SerializedName("ErrorCode")
    String ErrorCode;

    @SerializedName("Message")
    String Message;

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}