package com.khalilmohamed.diff2xml.service.implementation;

import com.khalilmohamed.diff2xml.service.Diff2XMLService;
import com.khalilmohamed.diff2xml.utils.FileUtils;
import com.khalilmohamed.diff2xml.utils.XMLUtils;
import com.khalilmohamed.diff2xml.utils.diff_match_patch;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlunit.diff.Difference;

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
        Document docFirst = XMLUtils.createDocumentFromPath(FIRST_FROM_PATH);
        Document docSecond = XMLUtils.createDocumentFromPath(SECOND_FROM_PATH);

        //Get all differences between two xml using XMLUnit and build lists for nodes inserted/deleted or with different value
        List<Difference> allDifferences = XMLUtils.getAllDifferences(xmlFirst,xmlSecond);
        List<Node> toInsertNodes = new ArrayList<>();

        for(Difference d : allDifferences){
            switch(d.getComparison().getType()){
                case TEXT_VALUE: //Replace only the text value of the field
                    if(d.getComparison().getTestDetails().getXPath() != null){
                        XMLUtils.getNodeInDocByXPath(d.getComparison().getTestDetails().getXPath(),docSecond)
                                .setTextContent(convertDiffObjectToXML(d));
                    }
                    break;
                case CHILD_LOOKUP: //Save the inserted node's copy  or create the deleted nodes
                    if(d.getComparison().getControlDetails().getValue() == null)
                        toInsertNodes.add(XMLUtils.getNodeInDocByXPath(d.getComparison().getTestDetails().getXPath(),
                                          docSecond));
                    else
                        XMLUtils.createDeletedNode(docFirst, docSecond,
                                                   d.getComparison().getControlDetails().getXPath(),
                                                   d.getComparison().getTestDetails().getParentXPath());
                    break;
            }
        }

        //Replace inserted nodes with parent node <ins> node </ins>
        for(Node node : toInsertNodes){
            XMLUtils.replaceNode(node, docSecond);
        }

        //Convert the document with the "git commit" visualization to string replacing the new escape characters
        if(XMLUtils.convertDocumentToString(docSecond) != null)
            return XMLUtils.convertDocumentToString(docSecond)
                    .replace("&lt;", "<")
                    .replace("&gt;",">");
        else
            return "";
    }

    private String convertDiffObjectToXML(Difference difference){
        diff_match_patch dmp = new diff_match_patch();
        String oldValue = (String) difference.getComparison().getControlDetails().getValue();
        String newValue = (String) difference.getComparison().getTestDetails().getValue();
        LinkedList<diff_match_patch.Diff> diff = dmp.diff_main(oldValue,newValue);
        dmp.diff_cleanupSemantic(diff);
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
