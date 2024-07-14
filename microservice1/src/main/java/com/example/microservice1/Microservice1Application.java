package com.example.microservice1;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.microservice1.service.DependencyUpdateService;

@SpringBootApplication
public class Microservice1Application /*implements CommandLineRunner*/ {
	/*@Autowired
    private DependencyUpdateService dependencyUpdateService;*/

    public static void main(String[] args) {
        SpringApplication.run(Microservice1Application.class, args);
    }

    /*@Override
    public void run(String... args) throws IOException {
        String currentDir = System.getProperty("user.dir");
        dependencyUpdateService.updateDependencies(currentDir);
    }*/
}
