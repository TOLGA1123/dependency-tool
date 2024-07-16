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

    public String getLastParentPomDetails(String pomFilePath) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(pomFilePath));
        String parentPomUrl = "";

        // Navigate through parent POMs
        while (model.getParent() != null) {

            parentPomUrl = String.format("https://repo1.maven.org/maven2/%s/%s/%s/%s-%s.pom",
                    model.getParent().getGroupId().replace('.', '/'),
                    model.getParent().getArtifactId(),
                    model.getParent().getVersion(),
                    model.getParent().getArtifactId(),
                    model.getParent().getVersion());

            try (InputStreamReader readerStream = new InputStreamReader(new URL(parentPomUrl).openStream())) {
                model = reader.read(readerStream);
            }
        }

        return parentPomUrl;
    }

    public String findDependencyVersion(String groupId, String artifactId, String parentPomUrl) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();

        try (InputStreamReader readerStream = new InputStreamReader(new URL(parentPomUrl).openStream())) {
            Model model = reader.read(readerStream);
            Properties properties = model.getProperties();
            // Check direct dependencies
            List<org.apache.maven.model.Dependency> dependencies = model.getDependencies();
            if (dependencies != null) {
                for (org.apache.maven.model.Dependency dependency : dependencies) {
                    String version = dependency.getVersion();
                    if (dependency.getGroupId().equals(groupId) && dependency.getArtifactId().equals(artifactId)) {
                        if (version.startsWith("${") && version.endsWith("}") && properties != null) {
                            String propertyName = version.substring(2, version.length() - 1);
                            String resolvedVersion = properties.getProperty(propertyName);
                            if (resolvedVersion != null) {
                                return resolvedVersion.trim();
                            } else {
                                // Handle case where property is not found
                                return null; // Or throw an exception as needed
                            }
                        } else {
                            // Version is already a concrete version number
                            return version.trim();
                        }
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
                        if (version.startsWith("${") && version.endsWith("}") && properties != null) {
                            String propertyName = version.substring(2, version.length() - 1);
                            String resolvedVersion = properties.getProperty(propertyName);
                            if (resolvedVersion != null) {
                                return resolvedVersion.trim();
                            } else {
                                // Handle case where property is not found
                                return null; // Or throw an exception as needed
                            }
                        } else {
                            // Version is already a concrete version number
                            return version.trim();
                        }
                    }
                    }
                }
            }
            if (properties != null) {
                String versionProperty = properties.getProperty(artifactId + ".version");
                if (versionProperty != null && !versionProperty.isEmpty()) {
                    return versionProperty.trim();
                }
            }
        } catch (IOException | XmlPullParserException e) {
            // Handle exceptions properly
            e.printStackTrace();
            throw e; // Propagate the exception or handle it according to your application's needs
        }

        return null; // Return null if dependency or version not found
    }
    
}
