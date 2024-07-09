package com.example.microservice1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.microservice1.service.DependencyUpdateService;
import com.example.microservice1.service.NexusService;

@SpringBootApplication
public class Microservice1Application implements CommandLineRunner {
	 @Autowired
    private DependencyUpdateService dependencyUpdateService;

    public static void main(String[] args) {
        SpringApplication.run(Microservice1Application.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        String currentDir = System.getProperty("user.dir");
        dependencyUpdateService.updateDependencies(currentDir);
    }
}
	/*@Autowired
    private PomFileScanner pomFileScanner;
    
    /*@Value("${project.root}")
    private String rootDirectoryPath;
    
    @Autowired
    private PomParser pomParser;

    @Autowired
    private NexusService nexusService;
	public static void main(String[] args) {
		SpringApplication.run(Microservice1Application.class, args);
	}
	@Override
    public void run(String... args) throws IOException {
        if (args.length < 1) {
            System.out.println("Please provide the root directory path.");
            return;
        }

        //String rootDirectoryPath = "C:\\Users\\USER\\Desktop\\dependency-tool";
        //String rootDirectoryPath = System.getProperty("user.dir");
        String currentDir = System.getProperty("user.dir");
        // Convert the string to a File object
        File currentDirFile = new File(currentDir);
        // Get the parent directory
        File parentDirFile = currentDirFile.getParentFile();
        System.out.println(parentDirFile);
        //List<File> pomFiles = pomFileScanner.findPomFiles(rootDirectoryPath);
        List<File> pomFiles = pomFileScanner.findPomFiles(parentDirFile.getAbsolutePath());
        System.out.println("Found POM files:");
        for (File pomFile : pomFiles) {
            System.out.println(pomFile.getAbsolutePath());
            List<Dependency> dependencies = pomParser.parseDependencies(pomFile);
            System.out.println("Dependencies:");
            for (Dependency dependency : dependencies) {
                System.out.println(dependency);
                 // Fetch dependencies for each group ID
                 String groupId = dependency.getGroupId();
                 List<String> fetchedDependencies = nexusService.fetchDependencies(groupId);
                 System.out.println("Fetched Dependencies for groupId " + groupId + ": " + fetchedDependencies);
            }
        }*/
        /*List<String> fetchedDependencies = nexusService.fetchDependencies("org.apache");
        System.out.println("Fetched Dependencies: " + fetchedDependencies);
    }
}*/
