# dependency-tool
 
Use the docker image for nexus for java 11 since orientDB does not support java 17
docker run -d -p 8081:8081 --name nexus sonatype/nexus3
username: admin
password: admin
settings.xml in USER/.m2 directory
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
<servers>
<server>
<id>nexus</id>
<username>admin</username>
<password>admin123</password>
<!--  Replace with your Nexus credentials  -->
</server>
</servers>
<profiles>
<profile>
<id>nexus</id>
<repositories>
<repository>
<id>central</id>
<url>http://localhost:8081/repository/maven-public/</url>
<releases>
<enabled>true</enabled>
</releases>
<snapshots>
<enabled>true</enabled>
</snapshots>
</repository>
</repositories>
<pluginRepositories>
<pluginRepository>
<id>central</id>
<url>http://localhost:8081/repository/maven-public/</url>
<releases>
<enabled>true</enabled>
</releases>
<snapshots>
<enabled>true</enabled>
</snapshots>
</pluginRepository>
</pluginRepositories>
</profile>
</profiles>
<activeProfiles>
<activeProfile>nexus</activeProfile>
</activeProfiles>
</settings>
maven install   + environment variables
mvn clean install in microservice1 directory
BUILD SUCCESS
It copies the packaged artifact (e.g., JAR file) to your local Maven repository (~/.m2/repository), making it available for other projects on your local machine that depend on it. C:\Users\USER\.m2\repository