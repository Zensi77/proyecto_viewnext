package com.juanma.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ELKSender {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objMapper = new ObjectMapper();

    @Value("${elk-url}")
    private String ELKURL;

    @Value("${elk.mapping.directory}")
    private String mappingDirectory;

    public void sentoELK(Map<String, Object> metric, EventEnum event) {
        checkIndex(event);
        try {
            String jsonData = objMapper.writeValueAsString(metric);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> req = new HttpEntity<>(jsonData, headers);

            restTemplate.postForObject(ELKURL + event.name().toLowerCase() + "/_doc", req, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> loadMapping(String mapper) throws IOException {
        ObjectMapper objMapper = new ObjectMapper();
        InputStream input = new ClassPathResource(this.mappingDirectory + mapper).getInputStream();

        return objMapper.readValue(input, Map.class);
    }

    private void createIndex(EventEnum event) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> mapping = loadMapping(event.name().toLowerCase() + ".json");

            HttpEntity<Map<String, Object>> req = new HttpEntity<>(mapping, headers);
            restTemplate.put(ELKURL + event.name().toLowerCase(), req, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkIndex(EventEnum event) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(ELKURL + event.name().toLowerCase(),
                    String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Index " + event.name().toLowerCase() + " already exists.");
            } else {
                createIndex(event);
            }
        } catch (Exception e) {
            createIndex(event);
        }
    }
}
