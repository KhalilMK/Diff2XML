package com.khalilmohamed.diff2xml.controller;

import com.khalilmohamed.diff2xml.service.Diff2XMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

@RestController
public class Diff2XMLController {

    @Autowired
    private Diff2XMLService diff2XMLService;
    @GetMapping(value = "/diffXML", produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> getDiffXML() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, TransformerException {
        String newXML = diff2XMLService.getDiffXML();

        return new ResponseEntity<>(newXML, HttpStatus.OK);
    }
}
