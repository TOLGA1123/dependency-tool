package com.example.microservice1.service;

import com.example.microservice1.Dependency;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NexusService {

    @Value("${nexus.url}")
    private String nexusUrl;

    @Value("${nexus.repository}")
    private String repository;

    private final ObjectMapper objectMapper;

    public NexusService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Dependency> fetchDependencies(String groupId) throws IOException {
        List<Dependency> dependencies = new ArrayList<>();
        String url = String.format("%s/service/rest/v1/search?repository=%s&group=%s", nexusUrl, repository, groupId);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                JsonNode rootNode = objectMapper.readTree(response.getEntity().getContent());
                JsonNode items = rootNode.path("items");
                if (items.isArray()) {
                    for (JsonNode item : items) {
                        String artifactId = item.path("artifactId").asText();
                        String version = item.path("version").asText();
                        if (version != null) {
                            dependencies.add(new Dependency(groupId, artifactId, version));
                        } else {
                            // Handle the case where version is null
                            dependencies.add(new Dependency(groupId, artifactId, "Unknown"));
                        }
                    }
                }
            }
        }

        return dependencies;
    }
}
