package com.antra.report.client.pojo.request;

import javax.validation.constraints.NotBlank;

public class ValidateRequest {
    @NotBlank
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}