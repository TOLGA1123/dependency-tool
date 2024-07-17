package com.example.microservice1.service;

import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Service;

import com.example.microservice1.Dependency;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Properties;

@Service
public class ParentPomReaderService {

    public String getParentPomDetails(String pomFilePath) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(pomFilePath));
        if (model.getParent() != null) {
            String parentPomUrl = String.format("https://repo1.maven.org/maven2/%s/%s/%s/%s-%s.pom",
                    model.getParent().getGroupId().replace('.', '/'),
                    model.getParent().getArtifactId(),
                    model.getParent().getVersion(),
                    model.getParent().getArtifactId(),
                    model.getParent().getVersion());
            return parentPomUrl;
        }
        return null;
    }

    public String findDependencyVersion(String groupId, String artifactId, String pomFilePath) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        System.out.println(pomFilePath);
        URL url = new URL(pomFilePath); // Convert the URL string to URL object
        try (InputStreamReader readerStream = new InputStreamReader(url.openStream())) {
            Model model = reader.read(readerStream);
            return findDependencyVersionRecursive(groupId, artifactId, model);
        }
    }
    

    private String findDependencyVersionRecursive(String groupId, String artifactId, Model model) throws IOException, XmlPullParserException {
        Properties properties = model.getProperties();

        // Check direct dependencies
        List<org.apache.maven.model.Dependency> dependencies = model.getDependencies();
        if (dependencies != null) {
            for (org.apache.maven.model.Dependency dependency : dependencies) {
                String version = dependency.getVersion();
                if (dependency.getGroupId().equals(groupId) && dependency.getArtifactId().equals(artifactId)) {
                    return resolveVersion(version, properties);
                }
            }
        }

        // Check managed dependencies in dependencyManagement
        DependencyManagement dependencyManagement = model.getDependencyManagement();
        if (dependencyManagement != null) {
            List<org.apache.maven.model.Dependency> managedDependencies = dependencyManagement.getDependencies();
            if (managedDependencies != null) {
                for (org.apache.maven.model.Dependency dependency : managedDependencies) {
                    String version = dependency.getVersion();
                    if (dependency.getGroupId().equals(groupId) && dependency.getArtifactId().equals(artifactId)) {
                        return resolveVersion(version, properties);
                    }
                }
            }
        }

        // Check properties for version
        if (properties != null) {
            String versionProperty = properties.getProperty(artifactId + ".version");
            if (versionProperty != null && !versionProperty.isEmpty()) {
                return versionProperty.trim();
            }
        }

        // If version not found, recursively check parent POMs
        if (model.getParent() != null) {
            String parentPomUrl = String.format("https://repo1.maven.org/maven2/%s/%s/%s/%s-%s.pom",
                    model.getParent().getGroupId().replace('.', '/'),
                    model.getParent().getArtifactId(),
                    model.getParent().getVersion(),
                    model.getParent().getArtifactId(),
                    model.getParent().getVersion());

            try (InputStreamReader readerStream = new InputStreamReader(new URL(parentPomUrl).openStream())) {
                Model parentModel = new MavenXpp3Reader().read(readerStream);
                return findDependencyVersionRecursive(groupId, artifactId, parentModel);
            }
        }

        return null;
    }

    private String resolveVersion(String version, Properties properties) {
        if (version.startsWith("${") && version.endsWith("}") && properties != null) {
            String propertyName = version.substring(2, version.length() - 1);
            String resolvedVersion = properties.getProperty(propertyName);
            if (resolvedVersion != null) {
                return resolvedVersion.trim();
            } else {
                return null;
            }
        } else {
            return version.trim();
        }
    }
}
