package com.example.microservice1.service;

import com.example.microservice1.Dependency;
import com.example.microservice1.PomFileScanner;
import com.example.microservice1.PomParser;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class DependencyUpdateService {

    @Autowired
    private PomFileScanner pomFileScanner;

    @Autowired
    private PomParser pomParser;

    @Autowired
    private NexusService nexusService;

    public void updateDependencies(String rootDir) throws IOException {
        File currentDirFile = new File(rootDir);
        File parentDirFile = currentDirFile.getParentFile();
        System.out.println(parentDirFile);

        List<File> pomFiles = pomFileScanner.findPomFiles(parentDirFile.getAbsolutePath());
        System.out.println("Found POM files:");
        for (File pomFile : pomFiles) {
            System.out.println(pomFile.getAbsolutePath());
            List<Dependency> dependencies = pomParser.parseDependencies(pomFile);
            System.out.println("Dependencies:");
            for (Dependency dependency : dependencies) {
                System.out.println(dependency);
                List<Dependency> fetchedDependencies = nexusService.fetchDependencies(dependency.getGroupId());
                System.out.println("Fetched Dependencies: " + fetchedDependencies);

                for (Dependency fetchedDependency : fetchedDependencies) {
                    //String[] parts = fetchedDependency.split(":");
                    String fetchedVersion = fetchedDependency.getVersion();

                    ComparableVersion currentVersion = new ComparableVersion(dependency.getVersion());
                    ComparableVersion latestVersion = new ComparableVersion(fetchedVersion);

                    if (currentVersion.compareTo(latestVersion) < 0) {
                        System.out.println("Newer version available for " + dependency + ": " + fetchedVersion);
                        // Logic to update the POM file with the new version
                    }
                }
            }
        }
    }
}
