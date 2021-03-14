package com.antra.report.client.pojo.reponse;

import com.antra.report.client.entity.DataEntity;

import java.util.List;

public class DataResponse {
    private String description;

    private List<String> headers;

    private List<List<String>> data;

    public DataResponse() {
    }

    public DataResponse(DataEntity entity) {
        this.description = entity.getDescription();
        this.headers = entity.getHeaders();
        this.data = entity.getData();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }
}