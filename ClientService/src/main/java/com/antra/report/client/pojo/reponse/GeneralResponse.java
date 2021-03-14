package com.antra.report.client.pojo.reponse;

import org.springframework.http.HttpStatus;

public class GeneralResponse {
    private HttpStatus statusCode;

    private Object data;

    private String message;

    public GeneralResponse() {
    }

    public GeneralResponse(HttpStatus statusCode, Object data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public GeneralResponse(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public GeneralResponse(HttpStatus statusCode, Object data, String message) {
        this.statusCode = statusCode;
        this.data = data;
        this.message = message;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
