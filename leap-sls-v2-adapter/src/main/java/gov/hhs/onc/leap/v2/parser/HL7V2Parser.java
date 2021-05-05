/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.onc.leap.v2.parser;

import com.google.common.base.Splitter;
import gov.hhs.onc.leap.sls.data.data.SimpleHCSConceptDescriptor;
import gov.hhs.onc.leap.v2.parser.data.SimpleClinicalFact;
import gov.hhs.onc.leap.v2.parser.data.V2SimpleBundle;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * ddecouteau@saperi.io
 */
public class HL7V2Parser {
    private final static Logger LOGGER = Logger.getLogger(HL7V2Parser.class.getName());
    private final String V2_PIPE = "|";
    private final String V2_HAT = "^";
    private final String LOINC_CODE_SYSTEM = "2.16.840.1.113883.6.1";
    private final String CPT_CODE_SYSTEM = " 2.16.840.1.113883.6.12";
    private final String SNOMEDCT_CODE_SYSTEM = "2.16.840.1.113883.6.96";    
    private final String UNIT_OF_MEASURE_SYSTEM = "http://unitsofmeasure.org";
    private final String ICD_10_CODE_SYSTEM = "ICD-10-CM ";
    private final String ICD_9_CODE_SYSTEM = "2.16.840.1.113883.6.103";
    private final String RXNORM_CODE_SYSTEM = "2.16.840.1.113883.6.88";
    
    private String messageType;
    private String id;
    private String metadata;
    private String messageDateTime;
    private String sendingOrganization;
    
    private String code;
    private String codeSystem;
    private String displayName;
    private SimpleHCSConceptDescriptor concept;
    private V2SimpleBundle bundle;
       
    public V2SimpleBundle parseMessage(String msg) {

            try {
                //split message by line
                Iterable<String> mshLines = Splitter.on(System.getProperty("line.separator")).split(msg);
                Iterator mshIter = mshLines.iterator();
                //set MSH Info
                String mshLine = (String)mshIter.next();
                setMessageInfoBundle(mshLine);
                
                Iterable<String> lines = Splitter.on(System.getProperty("line.separator")).split(msg);
                Iterator iter = lines.iterator();
                while (iter.hasNext()) {
                    String line = (String)iter.next();
                    Iterable<String> lineSegment = Splitter.on(V2_PIPE).split(line);
                    Iterator iter2 = lineSegment.iterator();
                    String segmentType = (String)iter2.next();

                    switch (segmentType) {
                        case "PV2":
                            metadata = "PV2";
                            concept = processPatientVisit(line);
                            if (concept.getCode() != null && !concept.getCode().isEmpty() && !concept.getCode().isBlank()) bundle.addClinicalFact(new SimpleClinicalFact(id, metadata, concept));
                            break;
                        case "DG1":
                            metadata = "DG1";
                            concept = processDiagnosis(line);
                            if (concept.getCode() != null && !concept.getCode().isEmpty() && !concept.getCode().isBlank()) bundle.addClinicalFact(new SimpleClinicalFact(id, metadata, concept));
                            break;
                        case "RXE":
                            metadata = "RXE";
                            concept = processMedication(line);
                            if (concept.getCode() != null && !concept.getCode().isEmpty() && !concept.getCode().isBlank()) bundle.addClinicalFact(new SimpleClinicalFact(id, metadata, concept));
                            break;
                        case "ORC":
                            metadata = "ORC";
                            concept = processCommonOrder(line);
                            if (concept.getCode() != null && !concept.getCode().isEmpty() && !concept.getCode().isBlank()) bundle.addClinicalFact(new SimpleClinicalFact(id, metadata, concept));
                            break;
                        case "OBX":
                            metadata = "OBX";
                            concept = processObservationResult(line);
                            if (concept.getCode() != null && !concept.getCode().isEmpty() && !concept.getCode().isBlank()) bundle.addClinicalFact(new SimpleClinicalFact(id, metadata, concept));
                            break;
                        case "OBR":
                            metadata = "OBR";
                            concept = processObservationRequest(line);
                            if (concept.getCode() != null || !concept.getCode().isEmpty() || !concept.getCode().isBlank()) bundle.addClinicalFact(new SimpleClinicalFact(id, metadata, concept));
                            break;
                        default:
                                 break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        
        return bundle;
    }
    
    private String setCodeSystem(String s) {
        String res = "";
        if (s != null) {
            if(s.equals("LN")) {
                res = LOINC_CODE_SYSTEM;
            }
            else if(s.equals("C4")) {
                res = CPT_CODE_SYSTEM;
            }
            else if(s.equals("SCT")) {
                res = SNOMEDCT_CODE_SYSTEM;
            }
            else if(s.equals("I9C")) {
                res = ICD_9_CODE_SYSTEM;
            }
            else if(s.equals("I10C")) {
                res = ICD_10_CODE_SYSTEM;
            }
            else {
                res = "Unknown";
            }
        } else {
            res = "Unknown";
        }
        return res;
    }
    
    private void setMessageInfoBundle(String line) {
        //process MSH for bundle  information
        
        Iterable<String> segments = Splitter.on(V2_PIPE).split(line);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String segment = (String)iter.next();
            switch (pos) {
                case 4:
                    //sending facility
                    sendingOrganization = setSendingOrganization(segment);
                    break;
                case 7:
                    messageDateTime = segment;
                    break;
                case 10:
                    //message control id
                    id = segment;
                    break;
                case 12:
                    messageType = segment;
                    break;
            //nothing
                default:
                    break;
            }
            pos++;
        }
        
        bundle = new V2SimpleBundle(UUID.randomUUID().toString(), sendingOrganization, messageType, messageDateTime);
    }
    
    private String setSendingOrganization(String line) {
        String res = "";
        Iterable<String> segments = Splitter.on(V2_HAT).split(line);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String s = (String)iter.next();
            if (pos == 2) {
                res = s;
                break;
            }
            else {
                //nothing here
            }
            pos++;
        }
        return res;
    }
    
    private SimpleHCSConceptDescriptor processPatientVisit(String line) {
        SimpleHCSConceptDescriptor concept = new SimpleHCSConceptDescriptor();
        Iterable<String> segments = Splitter.on(V2_PIPE).split(line);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String segment = (String)iter.next();
            if (pos == 4) {
                concept = setCE(segment);
            }
            pos++;
        }
        return concept;
    }
    
    private SimpleHCSConceptDescriptor processDiagnosis(String line) {
        SimpleHCSConceptDescriptor concept = new SimpleHCSConceptDescriptor();
        Iterable<String> segments = Splitter.on(V2_PIPE).split(line);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String segment = (String)iter.next();
            if (pos == 4) {
                concept = setCE(segment);
            }
            pos++;
        }
        
        return concept;
    }

    private SimpleHCSConceptDescriptor processMedication(String line) {
        SimpleHCSConceptDescriptor concept = new SimpleHCSConceptDescriptor();
        Iterable<String> segments = Splitter.on(V2_PIPE).split(line);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String segment = (String)iter.next();
            if (pos == 2) {
                concept = setCE(segment);
            }
            pos++;
        }
        return concept;
    }
    
    private SimpleHCSConceptDescriptor processObservationResult(String line) {
        SimpleHCSConceptDescriptor concept = new SimpleHCSConceptDescriptor();
        Iterable<String> segments = Splitter.on(V2_PIPE).split(line);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String segment = (String)iter.next();
            if (pos == 4) {
                concept = setCE(segment);
            }
            pos++;
        }
        return concept;
    }
    
    private SimpleHCSConceptDescriptor processObservationRequest(String line) {
        SimpleHCSConceptDescriptor concept = new SimpleHCSConceptDescriptor();
        Iterable<String> segments = Splitter.on(V2_PIPE).split(line);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String segment = (String)iter.next();
            if (pos == 4) {
                concept = setCE(segment);
            }
            pos++;
        }
        return concept;
    }

    private SimpleHCSConceptDescriptor processCommonOrder(String line) {
        SimpleHCSConceptDescriptor concept = new SimpleHCSConceptDescriptor();
        Iterable<String> segments = Splitter.on(V2_PIPE).split(line);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String segment = (String)iter.next();
            if (pos == 4) {
                concept = setCE(segment);
            }
            pos++;
        }
        return concept;
    }
    
    private SimpleHCSConceptDescriptor setCE(String segment) {
        //process code system
        SimpleHCSConceptDescriptor hcs = null;
        String ceCode = "";
        String ceDisplayName = "";
        String ceCodeSystem = "";
        Iterable<String> reason = Splitter.on(V2_HAT).split(segment);
        Iterator iter2 = reason.iterator();
        int field = 1;
        while (iter2.hasNext()) {
            String item = (String)iter2.next();
            switch (field) {
                case 1:
                    ceCode = item;
                    break;
                case 2:
                    ceDisplayName = item;
                    break;
                case 3:
                    String s = item;
                    ceCodeSystem = setCodeSystem(s);
                    break;
            //nothing here
                default:
                    break;
            }
            field++;
        } 
        hcs = new SimpleHCSConceptDescriptor(ceCodeSystem, ceCode, ceDisplayName);
        return hcs;
    }
    
}
