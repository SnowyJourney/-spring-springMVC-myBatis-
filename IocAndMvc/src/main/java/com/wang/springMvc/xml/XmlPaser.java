package com.wang.springMvc.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlPaser {

    public static List<String> getPackages(String springXml){
        ArrayList<String> packages = new ArrayList<>();
        SAXReader saxReader = new SAXReader();
        InputStream resourceAsStream =
                XmlPaser.class.getClassLoader().getResourceAsStream(springXml);
        try {
            Document xml = saxReader.read(resourceAsStream);
            Element rootElement = xml.getRootElement();
            List<Element> componentScan = rootElement.elements("component-scan");
            for (Element component : componentScan) {
                Attribute basePackage = component.attribute("base-package");
                packages.add(basePackage.getValue());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return packages;
    }
}
