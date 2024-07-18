package com.example.microservice1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.example.microservice1.Dependency;

@Component
public class PomUpdater {

    public void updateDependencyVersion(File pomFile, Dependency dependency, String newVersion) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(pomFile);

             // Parse properties
            Map<String, String> propertiesMap = new HashMap<>();
            NodeList propertiesNodes = document.getElementsByTagName("properties");
            if (propertiesNodes.getLength() > 0) {
                NodeList propertiesList = propertiesNodes.item(0).getChildNodes();
                for (int i = 0; i < propertiesList.getLength(); i++) {
                    Node node = propertiesList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        propertiesMap.put(node.getNodeName(), node.getTextContent());
                    }
                }
            }

            // Update dependencies
            NodeList dependencies = document.getElementsByTagName("dependency");
            for (int i = 0; i < dependencies.getLength(); i++) {
                Element element = (Element) dependencies.item(i);
                String groupId = element.getElementsByTagName("groupId").item(0).getTextContent();
                String artifactId = element.getElementsByTagName("artifactId").item(0).getTextContent();

                if (dependency.getGroupId().equals(groupId) && dependency.getArtifactId().equals(artifactId)) {
                    Node versionNode = element.getElementsByTagName("version").item(0);
                    if (versionNode != null) {
                        String version = versionNode.getTextContent();
                        if (version.startsWith("${") && version.endsWith("}")) {
                            // Update the property value
                            String propertyName = version.substring(2, version.length() - 1);
                            if (propertiesMap.containsKey(propertyName)) {
                                propertiesMap.put(propertyName, newVersion);
                                // Update the properties in the document
                                NodeList propertiesList = propertiesNodes.item(0).getChildNodes();
                                for (int j = 0; j < propertiesList.getLength(); j++) {
                                    Node node = propertiesList.item(j);
                                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(propertyName)) {
                                        node.setTextContent(newVersion);
                                    }
                                }
                            }
                        } else {
                            // Update the version directly
                            versionNode.setTextContent(newVersion);
                        }
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new FileWriter(pomFile));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
