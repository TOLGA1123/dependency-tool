package com.example.microservice1;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class PomFileScanner {

    public List<File> findPomFiles(String rootDirectoryPath) {
        List<File> pomFiles = new ArrayList<>();
        File rootDirectory = new File(rootDirectoryPath);
        if (rootDirectory.exists() && rootDirectory.isDirectory()) {
            findPomFiles(rootDirectory, pomFiles);
        }
        return pomFiles;
    }

    private void findPomFiles(File directory, List<File> pomFiles) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                findPomFiles(file, pomFiles);
            } else if (file.getName().equals("pom.xml")) {
                pomFiles.add(file);
            }
        }
    }
}
