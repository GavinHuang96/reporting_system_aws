package com.antra.report.client.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public interface RestCallService {
    public ResponseEntity<?> makeRestCall(String url, HttpMethod httpMethod, Object request, Class<?> response);
}