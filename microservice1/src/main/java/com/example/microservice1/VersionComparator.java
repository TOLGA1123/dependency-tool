package com.example.microservice1;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.stereotype.Component;
@Component
public class VersionComparator {

    public boolean isOutdated(String currentVersion, String latestVersion) {
        if (currentVersion == null || latestVersion == null) {
            return true; // Treat null version as outdated
        }
        ComparableVersion current = new ComparableVersion(currentVersion);
        ComparableVersion latest = new ComparableVersion(latestVersion);
        return current.compareTo(latest) < 0;
    }
    public void sortDependenciesByVersion(List<Dependency> dependencies) {
        Collections.sort(dependencies, new Comparator<Dependency>() {
            @Override
            public int compare(Dependency d1, Dependency d2) {
                ComparableVersion version1 = new ComparableVersion(d1.getVersion());
                ComparableVersion version2 = new ComparableVersion(d2.getVersion());
                return version1.compareTo(version2);
            }
        });
    }
}
