package com.tarento.notebook.models;

public class ResponseContainer {
    private String message;
    private String statusCode;
    private String statusMessage;

    public ResponseContainer(String message, String statusCode, String statusMessage) {
        this.message = message;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

}
