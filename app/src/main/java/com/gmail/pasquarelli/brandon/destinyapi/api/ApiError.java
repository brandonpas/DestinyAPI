package com.gmail.pasquarelli.brandon.destinyapi.api;

public class ApiError {

    private String error;

    private String description = "Unknown error";

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
