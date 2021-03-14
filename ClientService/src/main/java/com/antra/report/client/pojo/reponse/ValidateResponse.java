package com.antra.report.client.pojo.reponse;

public class ValidateResponse {
    private Boolean success;

    public ValidateResponse(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}