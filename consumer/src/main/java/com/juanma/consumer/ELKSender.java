package com.juanma.consumer;

import java.util.Map;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ELKSender {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objMapper = new ObjectMapper();
    private final String ELKURL = "http://localhost:9200/metrics/_doc";

    public void sentoELK(Map<String, Object> metric) {
        try {
            String jsonData = objMapper.writeValueAsString(metric);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> req = new HttpEntity<>(jsonData, headers);

            restTemplate.postForObject(ELKURL, req, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
