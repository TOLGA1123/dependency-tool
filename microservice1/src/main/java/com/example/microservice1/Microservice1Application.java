package com.example.microservice1;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Microservice1Application implements CommandLineRunner {
	 
	@Autowired
    private PomFileScanner pomFileScanner;
    @Value("${project.root}")
    private String rootDirectoryPath;
    
	public static void main(String[] args) {
		SpringApplication.run(Microservice1Application.class, args);
	}
	@Override
    public void run(String... args) {
        /*if (args.length < 1) {
            System.out.println("Please provide the root directory path.");
            return;
        }*/

        //String rootDirectoryPath = "C:\\Users\\USER\\Desktop\\dependency-tool";
        //String rootDirectoryPath = System.getProperty("user.dir");
        List<File> pomFiles = pomFileScanner.findPomFiles(rootDirectoryPath);

        System.out.println("Found POM files:");
        for (File pomFile : pomFiles) {
            System.out.println(pomFile.getAbsolutePath());
        }
    }
}
