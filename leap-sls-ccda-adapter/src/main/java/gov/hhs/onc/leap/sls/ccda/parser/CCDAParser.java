package gov.hhs.onc.leap.sls.ccda.parser;


import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import gov.hhs.onc.leap.sls.ccda.parser.data.CCDASimpleBundle;
import gov.hhs.onc.leap.sls.data.data.SimpleHCSConceptDescriptor;
import gov.hhs.onc.leap.sls.ccda.parser.data.PlaintextClinicalFactAdapter;
import gov.hhs.onc.leap.sls.ccda.parser.data.SimpleClinicalFact;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ddecouteau@saperi.io
 */
public class CCDAParser {

    private final static Logger LOGGER = Logger.getLogger(CCDAParser.class.getName());

    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    CCDANamespaceProvider namespaces = new CCDANamespaceProvider();

    DocumentBuilder builder;
    XPath xpath;

    public CCDAParser () throws ParserConfigurationException
    {
        domFactory.setNamespaceAware(true);
        builder = domFactory.newDocumentBuilder();
        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(namespaces);

    }

    public CCDASimpleBundle parseCodesFromFile(File file) throws IOException, SAXException, XPathExpressionException {
        FileInputStream fis = new FileInputStream(file);
        return parseCodesFromInputStream(fis, file.getAbsolutePath());
    }

    public CCDASimpleBundle parseCodesFromInputStream (InputStream ccdaSource, String documentIdentifier) throws IOException, SAXException, XPathExpressionException {

        LOGGER.log(Level.INFO, String.format("CCDA Parsing: %s", documentIdentifier) );
        Document doc = builder.parse(ccdaSource);
        XPathExpression codeQuery = xpath.compile("//ccda:value[@code and @codeSystem]");
        NodeList codeNodes = (NodeList) codeQuery.evaluate(doc, XPathConstants.NODESET);
        LOGGER.log(Level.INFO, String.format("CCDA Parsing: %s : code nodes found: %d", documentIdentifier, codeNodes.getLength()) );


        XPathExpression translationCodeQueries = xpath.compile("//ccda:translation[@code and @codeSystem]");
        NodeList translationCodeNodes = (NodeList) translationCodeQueries.evaluate(doc, XPathConstants.NODESET);
        LOGGER.log(Level.INFO, String.format("CCDA Parsing: %s : translation code nodes found: %d", documentIdentifier, translationCodeNodes.getLength()) );

        List<Node> allCodeNodes = new ArrayList<Node>();

        for (int i = 0; i < codeNodes.getLength(); i++)
            allCodeNodes.add(codeNodes.item(i));

        for (int i=0; i < translationCodeNodes.getLength(); i++)
            allCodeNodes.add(translationCodeNodes.item(i));



        CCDASimpleBundle ccdaBundle = new CCDASimpleBundle(documentIdentifier);


        for (int i = 0; i < allCodeNodes.size(); i++) {
            Node node = allCodeNodes.get(i);

            String path = "";
            Node parent = node.getParentNode();
            while (parent != null)
            {
                String parentName = parent.getNodeName();

                if ("section".equals(parentName)) {
                    NodeList childNodes = parent.getChildNodes();
                    for (int k = 0; k < childNodes.getLength(); k++)
                    {
                        Node child = childNodes.item(k);
                        if ("title".equals(child.getNodeName()))
                        {
                            path = " [" + child.getTextContent() + "]" + path;
                        }
                    }
                    path =  parentName + path;
                    break;
                }
                parent = parent.getParentNode();
            }

            NamedNodeMap attributes = node.getAttributes();

            String code = null;
            String codeSystem = null;
            String displayName = null;
            //String codeSystemName = null;
            HCSConceptDescriptor conceptDescriptor = null;

            if ( attributes.getNamedItem("code") != null)
                code = attributes.getNamedItem("code").getNodeValue();
            if (attributes.getNamedItem("codeSystem") != null)
                codeSystem = attributes.getNamedItem("codeSystem").getNodeValue();
            if (attributes.getNamedItem("displayName") !=null )
                displayName = attributes.getNamedItem("displayName").getNodeValue();
            //if (attributes.getNamedItem("codeSystemName") !=null )
            // codeSystemName = attributes.getNamedItem("codeSystemName").getNodeValue();

            if (code != null && codeSystem != null)
            {
                if (displayName != null)
                    conceptDescriptor =  new SimpleHCSConceptDescriptor(codeSystem, code, displayName);
                else
                    conceptDescriptor =  new SimpleHCSConceptDescriptor(codeSystem, code);

            }

            String id = documentIdentifier + "/" + i;

            if (conceptDescriptor != null) {
                ccdaBundle.addClinicalFact(new SimpleClinicalFact(id, path, conceptDescriptor));
            }
        }

        return ccdaBundle;

    }

    public CCDASimpleBundle parseTextSectionsFromInputStream (InputStream ccdaSource, String documentIdentifier) throws IOException, SAXException, XPathExpressionException {

        TextChunker textChunker = new TextChunker();

        CCDASimpleBundle ccdaBundle = new CCDASimpleBundle(documentIdentifier);

        Document doc = builder.parse(ccdaSource);
        XPathExpression textQuery = xpath.compile("//ccda:section/ccda:text");

        NodeList textNodes = (NodeList) textQuery.evaluate(doc, XPathConstants.NODESET);
        LOGGER.log(Level.INFO, String.format("CCDA Parsing: %s : text nodes found: %d", documentIdentifier, textNodes.getLength()) );


        for (int i = 0; i < textNodes.getLength(); i++) {
            Node node = textNodes.item(i);
            Node parent = node.getParentNode();
            String path = "";
            while (parent != null)
            {
                String parentName = parent.getNodeName();

                if ("section".equals(parentName)) {
                    NodeList childNodes = parent.getChildNodes();
                    for (int k = 0; k < childNodes.getLength(); k++)
                    {
                        Node child = childNodes.item(k);
                        if ("title".equals(child.getNodeName()))
                        {
                            path = " [" + child.getTextContent() + "]" + path;
                        }
                    }
                    path =  parentName + path;
                    break;
                }
                //path =  parentName+ "/" + path;
                parent = parent.getParentNode();
            }
            String text = node.getTextContent();
            System.out.println();
            if (text != null && !"".equals(text.trim())) {
                List<String> textChunks =  textChunker.getChunks(text);
                for (int j = 0; j < textChunks.size(); j++ )
                {
                    ccdaBundle.addPlainTextClinicalFact(new PlaintextClinicalFactAdapter(documentIdentifier + "/text/" + i +"[" +j+"]" , textChunks.get(j), path));
                }
            }
            //System.out.println(node.getTextContent());
        }

        return ccdaBundle;
    }

    public CCDASimpleBundle parseTextSectionsFromInputStreamIgnoreVAQuestionnaires (InputStream ccdaSource, String documentIdentifier) throws IOException, SAXException, XPathExpressionException {
        TextChunker textChunker = new TextChunker();

        CCDASimpleBundle ccdaBundle = new CCDASimpleBundle(documentIdentifier);

        Document doc = builder.parse(ccdaSource);

        LOGGER.log(Level.INFO, String.format("CCDA Parsing: %s : removing VA Questionnaires", documentIdentifier) );
        removeQuestionnaireChildren(doc.getDocumentElement(), documentIdentifier);

        XPathExpression textQuery = xpath.compile("//ccda:section/ccda:text");
        NodeList textNodes = (NodeList) textQuery.evaluate(doc, XPathConstants.NODESET);
        LOGGER.log(Level.INFO, String.format("CCDA Parsing: %s : text nodes found: %d", documentIdentifier, textNodes.getLength()) );

        for (int i = 0; i < textNodes.getLength(); i++) {
            Node node = textNodes.item(i);

            removeQuestionnaireChildren(node, documentIdentifier);

            Node parent = node.getParentNode();
            String path = "";
            while (parent != null)
            {
                String parentName = parent.getNodeName();

                if ("section".equals(parentName)) {
                    NodeList childNodes = parent.getChildNodes();
                    for (int k = 0; k < childNodes.getLength(); k++)
                    {
                        Node child = childNodes.item(k);
                        if ("title".equals(child.getNodeName()))
                        {
                            path = " [" + child.getTextContent() + "]" + path;
                        }
                    }
                    path =  parentName + path;
                    break;
                }
                //path =  parentName+ "/" + path;
                parent = parent.getParentNode();
            }
            String text = node.getTextContent();
//            System.out.println();
            if (text != null && !"".equals(text.trim())) {
                List<String> textChunks =  textChunker.getChunks(text);
                for (int j = 0; j < textChunks.size(); j++ )
                {
                    ccdaBundle.addPlainTextClinicalFact(new PlaintextClinicalFactAdapter(documentIdentifier + "/text/" + i +"[" +j+"]" , textChunks.get(j), path));
                }
            }
        }

        return ccdaBundle;
    }

    //a questionnaire is a "content" node with the word "questionnaire" in its body.
    private void removeQuestionnaireChildren (Node node, String documentIdentifier)
    {
        if (node instanceof Element) {
            NodeList childNodes = ((Element)node).getElementsByTagName("content");
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                String nodeContent = childNode.getTextContent();
                if (StringUtils.contains(StringUtils.lowerCase(nodeContent), "questionnaire")) {
                    Node parent = childNode.getParentNode();
                    parent.removeChild(childNode);
                    LOGGER.log(Level.INFO, String.format("CCDA Parsing: %s : questionnaire removed.", documentIdentifier) );

                    //System.out.println("");
                    //numberOfQsDetected++;
                }
            }
        }

    }

    public static void main(String[] args) throws ParserConfigurationException, XPathExpressionException, SAXException, IOException {

//        CCDASimpleBundle ccdaSimpleBundle1 = new CCDAParser().parseCodesFromInputStream(new FileInputStream("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\sample_02.xml"), "sample_02.xml");
//        CCDASimpleBundle ccdaSimpleBundle2 = new CCDAParser().parseTextSectionsFromInputStream(new FileInputStream("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\sample_02.xml"), "sample_02.xml");
//        CCDASimpleBundle ccdaSimpleBundle2 = new CCDAParser().parseTextSectionsFromInputStream(new FileInputStream("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\95.xml"), "95.xml");
//        CCDASimpleBundle ccdaSimpleBundle2 = new CCDAParser().parseTextSectionsFromInputStreamIgnoreVAQuestionnaires(new FileInputStream("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\109.xml"), "95.xml");

        List<String> fileNames = new ArrayList<>();
//        File dir = new File("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\42.xml");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\139.xml");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\137.xml");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\132.xml");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\160.xml");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\153.xml");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\68.xml");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\136.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\140.xml");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\120.xml");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\156.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\149.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\32.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\130.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\94.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\5.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\123.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\157.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\95.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\131.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\158.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\147.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\135.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\124.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\133.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\143.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\85.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\146.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\6a.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\6N.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\58.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\127.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\125.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\154.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\152.xml");
        fileNames.add( "C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\154a.xml");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\122.xml");
        fileNames.add("C:\\Moka\\Code\\ccda-parser\\src\\main\\resources\\samples\\CCDA samples\\1.xml");

        for (String listedFileName : fileNames)
        {
            File listedFile = new File(listedFileName);

            if (listedFile.getName().endsWith(".xml")) {
                CCDASimpleBundle ccdaSimpleBundle = new CCDAParser().parseTextSectionsFromInputStreamIgnoreVAQuestionnaires(new FileInputStream(listedFile.getAbsolutePath()), listedFile.getName());
            }
        }



        System.out.println();


    }
}
