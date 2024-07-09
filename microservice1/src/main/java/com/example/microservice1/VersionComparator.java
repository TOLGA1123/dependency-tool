package com.example.microservice1;

import java.util.Comparator;
import org.apache.maven.artifact.versioning.ComparableVersion;

public class VersionComparator {

    public static boolean isOutdated(String currentVersion, String latestVersion) {
        if (currentVersion == null || latestVersion == null) {
            return true; // Treat null version as outdated
        }
        ComparableVersion current = new ComparableVersion(currentVersion);
        ComparableVersion latest = new ComparableVersion(latestVersion);
        return current.compareTo(latest) < 0;
    }
}
