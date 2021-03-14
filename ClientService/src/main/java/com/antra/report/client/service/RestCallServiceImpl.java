package com.antra.report.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestCallServiceImpl implements RestCallService {
    private RestTemplate restTemplate;

    @Autowired
    public RestCallServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> makeRestCall(String url, HttpMethod httpMethod, Object request, Class<?> response) {
        return restTemplate.exchange(url, httpMethod, new HttpEntity<>(request), response, "");
    }
}