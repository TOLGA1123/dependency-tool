package com.example.microservice1.service;

import com.example.microservice1.Dependency;
import com.example.microservice1.PomFileScanner;
import com.example.microservice1.PomParser;
import com.example.microservice1.PomUpdater;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class DependencyUpdateService {

    @Autowired
    private PomFileScanner pomFileScanner;

    @Autowired
    private PomParser pomParser;

    @Autowired
    private NexusService nexusService;

    @Autowired
    private PomUpdater pomUpdater;

    public void updateDependencies(String rootDir) throws IOException {
        File currentDirFile = new File(rootDir);
        //File parentDirFile = currentDirFile.getParentFile();
        //log.info("Parent directory: {}", parentDirFile);

        List<File> pomFiles = pomFileScanner.findPomFiles(currentDirFile.getAbsolutePath());
        log.info("Found POM files:");
        for (File pomFile : pomFiles) {
            log.info(pomFile.getAbsolutePath());
            List<Dependency> dependencies = pomParser.parseDependencies(pomFile);
            log.info("Dependencies:");
            for (Dependency dependency : dependencies) {
                log.info("{}", dependency);
                if(dependency.getVersion().equals("Unknown")){
                    continue;
                }
                List<Dependency> fetchedDependencies = nexusService.fetchDependencies(dependency.getGroupId(), dependency.getArtifactId());
                log.info("Fetched Dependencies: ");
                for(Dependency fetched: fetchedDependencies){
                    log.info("{}", fetched);
                }
                boolean updated = false;
                /*for (Dependency fetchedDependency : fetchedDependencies) {
                    //String[] parts = fetchedDependency.split(":");*/
                    int lastIndex = fetchedDependencies.size() - 1;     //Fetched dependencies is already sorted with version number
                    Dependency latestDependency = fetchedDependencies.get(lastIndex);
                    String fetchedVersion = latestDependency.getVersion();
                    ComparableVersion currentVersion = new ComparableVersion(dependency.getVersion());
                    ComparableVersion latestVersion = new ComparableVersion(fetchedVersion);

                    if (currentVersion.compareTo(latestVersion) < 0) {
                        log.info("Newer version available for " + dependency + ": " + fetchedVersion);
                        pomUpdater.updateDependencyVersion(pomFile, dependency, fetchedVersion);
                        log.info("Dependency " + dependency + " updated with never version: " + fetchedVersion);
                        updated = true;
                    }
                //}
                if(!updated){
                    log.info(dependency + " is already latest version available.");
                }
            }
        }
    }
}
