package com.antra.report.client.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DataEntity {
    private String id;

    private String description;

    private List<String> headers;

    private List<List<String>> data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<List<String>> getData() {
        return data;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public String getDescription() {
        return description;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }
}