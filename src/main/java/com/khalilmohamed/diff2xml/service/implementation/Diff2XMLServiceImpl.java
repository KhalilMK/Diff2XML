package com.khalilmohamed.diff2xml.service.implementation;

import com.khalilmohamed.diff2xml.model.DiffObject;
import com.khalilmohamed.diff2xml.service.Diff2XMLService;
import com.khalilmohamed.diff2xml.utils.FileUtils;
import com.khalilmohamed.diff2xml.utils.XMLUtils;
import com.khalilmohamed.diff2xml.utils.diff_match_patch;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class Diff2XMLServiceImpl implements Diff2XMLService {

    private final String FIRST_FROM_PATH = "C:\\Users\\HOME\\Documents\\diff2xml\\src\\main\\resources\\input\\xmlFirst.xml";
    private final String SECOND_FROM_PATH = "C:\\Users\\HOME\\Documents\\diff2xml\\src\\main\\resources\\input\\xmlSecond.xml";
    @Override
    public String getDiffXML() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        //Read the content of the two files xml
        String xmlFirst = FileUtils.readFile(FIRST_FROM_PATH);
        String xmlSecond = FileUtils.readFile(SECOND_FROM_PATH);

        //Create DOCUMENT of the second file (output)
        Document docOutput = XMLUtils.createDocumentFromPath(SECOND_FROM_PATH);

        //Get all differences between two xml using XMLUnit and build diffObjects using diff_match_patch
        List<Difference> allDifferences = getAllDifferences(xmlFirst,xmlSecond);
        List<DiffObject> diffObjects = buildDiffObjects(allDifferences);

        //For each diffObjects, replace the node with the "git commit" visualization
        for(DiffObject d : diffObjects){
            XMLUtils.createDifferenceNodeFromString(convertDiffObjectToXML(d),
                                                    d.getXpathLocation(),
                                                    docOutput);
        }

        //Convert the document with the "git commit" visualization to string replacing the new escape characters
        if(XMLUtils.convertDocumentToString(docOutput) != null)
            return XMLUtils.convertDocumentToString(docOutput)
                    .replace("&lt;", "<")
                    .replace("&gt;",">");
        else
            return "";
    }

    private List<Difference> getAllDifferences(String xmlFirst, String xmlSecond) throws IOException, SAXException {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(xmlFirst, xmlSecond));

        return diff.getAllDifferences();
    }

    private List<DiffObject> buildDiffObjects(List<Difference> differences){
        diff_match_patch dmp = new diff_match_patch();
        List<DiffObject> diffObjects = new ArrayList<>();
        for(Difference d : differences){
                String oldValue = d.getControlNodeDetail().getValue();
                String newValue = d.getTestNodeDetail().getValue();
                LinkedList<diff_match_patch.Diff> diff = dmp.diff_main(oldValue, newValue);
                dmp.diff_cleanupSemantic(diff);

                DiffObject diffObject = DiffObject
                        .builder()
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .xpathLocation(d.getTestNodeDetail().getXpathLocation())
                        .differences(diff)
                        .build();
                diffObjects.add(diffObject);
        }
        return diffObjects;
    }

    private String convertDiffObjectToXML(DiffObject diffObject){
        LinkedList<diff_match_patch.Diff> diff = diffObject.getDifferences();
        String xml = "";

        for(diff_match_patch.Diff d : diff){
            switch (d.operation){
                case INSERT:
                    xml += "<ins>"+ d.text+"</ins>";
                    break;
                case EQUAL:
                    xml += d.text;
                    break;
                case DELETE:
                    xml += "<del>"+ d.text+"</del>";
                    break;
            }
        }
        return xml;
    }
}
