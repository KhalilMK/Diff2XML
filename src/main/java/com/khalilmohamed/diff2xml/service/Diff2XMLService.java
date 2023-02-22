package com.khalilmohamed.diff2xml.service;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public interface Diff2XMLService {
    String getDiffXML() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException;
}
