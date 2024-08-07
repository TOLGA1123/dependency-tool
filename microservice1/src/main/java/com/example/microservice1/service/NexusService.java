package com.example.microservice1.service;

import com.example.microservice1.Dependency;
import com.example.microservice1.VersionComparator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NexusService {

    @Value("${nexus.url}")
    private String nexusUrl;

    @Value("${nexus.repository}")
    private String[] nexusRepositories;

    private final ObjectMapper objectMapper;

    @Autowired
    VersionComparator versionComparator;

    public NexusService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Dependency> fetchDependencies(String groupId, String artifactId) throws IOException {
        List<Dependency> dependencies = new ArrayList<>();
        for (String repository : nexusRepositories) {
            log.info("Searching repository for dependency versions: {}\n", repository);
            String url = String.format("%s/service/rest/v1/search?repository=%s&group=%s&name=%s", nexusUrl, repository, groupId, artifactId);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);
                try (CloseableHttpResponse response = client.execute(request)) {
                    JsonNode rootNode = objectMapper.readTree(response.getEntity().getContent());
                    JsonNode items = rootNode.path("items");
                    if (items.isArray()) {
                        for (JsonNode item : items) {
                            String artifactId1 = item.path("artifactId").asText();
                            JsonNode versionNode = item.path("version");
                            String version = versionNode.isMissingNode() ? "Unknown" : versionNode.asText();
                            dependencies.add(new Dependency(groupId, artifactId1, version));
                            /*if (version != null) {
                                dependencies.add(new Dependency(groupId, artifactId, version));
                            } else {
                                // Handle the case where version is null
                                dependencies.add(new Dependency(groupId, artifactId, "Unknown"));
                            }*/
                        }
                    }
                }
            }
        }
        
        versionComparator.sortDependenciesByVersion(dependencies);
        return dependencies;
    }
}
