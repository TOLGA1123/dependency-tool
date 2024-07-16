package com;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.microservice1.Dependency;
import com.example.microservice1.PomFileScanner;
import com.example.microservice1.PomParser;
import com.example.microservice1.PomUpdater;
import com.example.microservice1.service.DependencyUpdateService;
import com.example.microservice1.service.NexusService;

public class DependencyUpdateServiceTest {

    @InjectMocks
    private DependencyUpdateService dependencyUpdateService;

    @Mock
    private PomFileScanner pomFileScanner;

    @Mock
    private PomParser pomParser;

    @Mock
    private NexusService nexusService;

    @Mock
    private PomUpdater pomUpdater;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateDependencies() throws IOException, XmlPullParserException {
        String rootDir = "testRootDir";
        File mockPomFile = new File("mockPom.xml");
        List<File> mockPomFiles = Arrays.asList(mockPomFile);
        Dependency mockDependency = new Dependency("groupId", "artifactId", "1.0.0");
        List<Dependency> mockDependencies = Arrays.asList(mockDependency);
        Dependency fetchedDependency = new Dependency("groupId", "artifactId", "1.1.0");
        List<Dependency> fetchedDependencies = Arrays.asList(fetchedDependency);

        when(pomFileScanner.findPomFiles(anyString())).thenReturn(mockPomFiles);
        when(pomParser.parseDependencies(any(File.class))).thenReturn(mockDependencies);
        when(nexusService.fetchDependencies(anyString(), anyString())).thenReturn(fetchedDependencies);

        dependencyUpdateService.updateDependencies(rootDir);

        verify(pomUpdater, times(1)).updateDependencyVersion(mockPomFile, mockDependency, "1.1.0");
    }
}
