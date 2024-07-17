package com.example.microservice1.controller;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import com.example.microservice1.Dependency;
import com.example.microservice1.service.DependencyUpdateService;
import com.example.microservice1.service.ParentPomReaderService;

@RestController
@Slf4j
public class DependencyController {

    @Autowired
    private DependencyUpdateService dependencyUpdateService;

    @Autowired
    private ParentPomReaderService parentPomService;

    // Update dependencies for a specific microservice
    @GetMapping("/update/{microserviceName}")       //http://localhost:8080/update/microservice2 or http://localhost:8080/update/microservice1
    public String updateDependencies(@PathVariable String microserviceName) throws XmlPullParserException {
        try {
            String currentDir = System.getProperty("user.dir");
            String[] dir = currentDir.split("microservice1");
            String microserviceDir = dir[0] + File.separator + microserviceName;
            System.out.println(microserviceDir);
            dependencyUpdateService.updateDependencies(microserviceDir);
            return "Dependencies updated successfully for microservice: " + microserviceName;
        } catch (IOException e) {
            log.error("Error updating dependencies: {}", e.getMessage());
            return "Failed to update dependencies for microservice: " + microserviceName + ". Check logs for details.";
        }
    }
     // Update dependencies for all microservices
     @GetMapping("/update/all")
     public String updateAllMicroservices() throws XmlPullParserException {
         try{
            String currentDir = System.getProperty("user.dir");
            File currentDirFile = new File(currentDir);
            File parentDirFile = currentDirFile.getParentFile();
             
            dependencyUpdateService.updateDependencies(parentDirFile.getAbsolutePath());
            log.info("Dependencies updated successfully for all microservices in : {}", parentDirFile.getName());
            return "Successfully updated dependencies for all microservices.";
        } catch (IOException e) {
             log.error("Error updating dependencies for all microservices: {}", e.getMessage());
             return "Failed to update dependencies for all microservices. Check logs for details.";
        }
     }
      @GetMapping("/parent-pom-details/{pomFilePath}")
    public String getParentPomDetails(@PathVariable String pomFilePath) {
        try {
            return parentPomService.getParentPomDetails(pomFilePath);
        } catch (IOException | XmlPullParserException e) {
            log.error("Error fetching parent POM details: {}", e.getMessage());
            return null;
        }
    }
}
