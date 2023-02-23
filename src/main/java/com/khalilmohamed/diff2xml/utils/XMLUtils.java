package com.khalilmohamed.diff2xml.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class XMLUtils {

    public static Node getNodeInDocByXPath(String xPathLocation, Document document) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();

        return (Node) xpath.evaluate(xPathLocation, document, XPathConstants.NODE);
    }

    public static Document createDocumentFromPath(String path) throws ParserConfigurationException, IOException, SAXException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                new InputSource(path));
    }

    public static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Difference> getAllDifferences(String xmlFirst, String xmlSecond) {
        //Nested logic of the element selector
        ElementSelector field = buildElementSelector("field", "code", ElementSelectors.byName);
        ElementSelector tab  = buildElementSelector("tab", "id", field);
        ElementSelector property = buildElementSelector("property", "code", tab);

        Diff diff = DiffBuilder
                        .compare(xmlFirst)
                        .withTest(xmlSecond)
                        .ignoreWhitespace().ignoreElementContentWhitespace().ignoreComments()
                        .withNodeMatcher(new DefaultNodeMatcher(property))
                        .build();

        return (List<Difference>) diff.getDifferences();
    }

    public static ElementSelector buildElementSelector(String elementName, String attribute, ElementSelector innerSel){
        //Two nodes with a given name are comparable if they have the same value of a given attribute or if they satisfy the innerselector
        return ElementSelectors.conditionalBuilder()
                .whenElementIsNamed(elementName)
                .thenUse(ElementSelectors.byNameAndAttributes(attribute))
                .elseUse(innerSel).build();
    }

    public static Node createDeletedNode(Document fromDoc, Document toDoc, String xpath, String parentXpath) throws XPathExpressionException {
        Node innerNode = toDoc.adoptNode(getNodeInDocByXPath(xpath, fromDoc).cloneNode(true));
        Element opElement = toDoc.createElement("del");
        opElement.appendChild(innerNode);
        getNodeInDocByXPath(parentXpath, toDoc).appendChild(opElement);
        return getNodeInDocByXPath(xpath, fromDoc);
    }

    public static void replaceNode(Node node, Document document){
        Node innerNode = node.cloneNode(true);
        Element insElement = document.createElement("ins");
        insElement.appendChild(innerNode);
        node.getParentNode().replaceChild(insElement, node);
    }
}
