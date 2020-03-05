package com.tarento.notebook.utility;

public enum JsonKey {

    STATUS_CODE("code"),
    STATUS_MESSAGE("message"),
    RESPONSE_DATA("data"),
    ERROR_MESSAGE("error");

    private String message;

    JsonKey(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
