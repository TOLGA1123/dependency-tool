package com.example.microservice1;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class PomParser {

    public List<Dependency> parseDependencies(File pomFile) {
        List<Dependency> dependencies = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // Make the factory namespace-aware
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(pomFile);
            document.getDocumentElement().normalize();

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            xPath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    if (prefix == null) {
                        throw new NullPointerException("Null prefix");
                    } else if ("".equals(prefix)) {
                        return "http://maven.apache.org/POM/4.0.0";
                    } else if ("pom".equals(prefix)) {
                        return "http://maven.apache.org/POM/4.0.0";
                    }
                    return null;
                }

                public String getPrefix(String namespaceURI) {
                    return null;
                }

                public Iterator getPrefixes(String namespaceURI) {
                    return null;
                }
            });

            NodeList dependencyNodes = (NodeList) xPath.evaluate("//pom:dependency", document, XPathConstants.NODESET);
            for (int i = 0; i < dependencyNodes.getLength(); i++) {
                Element dependencyElement = (Element) dependencyNodes.item(i);

                String groupId = getElementTextContent(dependencyElement, "groupId", xPath);
                String artifactId = getElementTextContent(dependencyElement, "artifactId", xPath);
                String version = getElementTextContent(dependencyElement, "version", xPath);

                if (groupId != null && artifactId != null) {
                    dependencies.add(new Dependency(groupId, artifactId, version));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            e.printStackTrace();
        }
        return dependencies;
    }

    private String getElementTextContent(Element parent, String tagName, XPath xPath) throws XPathExpressionException {
        NodeList nodeList = (NodeList) xPath.evaluate("pom:" + tagName, parent, XPathConstants.NODESET);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}
