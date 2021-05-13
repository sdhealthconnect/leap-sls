package gov.hhs.onc.leap.sls.impl.ruledb;

import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import gov.hhs.onc.leap.sls.data.data.SimpleHCSConceptDescriptor;
import gov.hhs.onc.leap.sls.impl.sls.MockSLSRule;
import gov.hhs.onc.leap.sls.impl.sls.MockSLSRulesDB;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ddecouteau@saperi.io
 */
@Component
public class CSVBasedRestrictedCodesSLSRulesDB extends MockSLSRulesDB {
    private final static Logger LOGGER = Logger.getLogger(CSVBasedRestrictedCodesSLSRulesDB.class.getName());

    static HCSConceptDescriptor[] RESTRICTED_CODE
            = new SimpleHCSConceptDescriptor[] {new SimpleHCSConceptDescriptor ("http://hl7.org/fhir/v3/Confidentiality", "R", "Restricted")};

    List<HCSConceptDescriptor> codeList = new ArrayList<>();

    @Autowired
    public CSVBasedRestrictedCodesSLSRulesDB( java.net.URL csvRestrictedCodesFileName) throws IOException
    {
        this(csvRestrictedCodesFileName.openStream());
    
    }
    
    private CSVBasedRestrictedCodesSLSRulesDB(File csvRestrictedCodesFileName) throws IOException
    {
        this(new FileInputStream(csvRestrictedCodesFileName));
    }
    
    public CSVBasedRestrictedCodesSLSRulesDB(InputStream csvRestrictedCodesFileName) throws IOException
    {

        Reader csvFileReader = new InputStreamReader(csvRestrictedCodesFileName);
        CSVParser parser = new CSVParser(csvFileReader, CSVFormat.EXCEL);
        List<CSVRecord> ruleList = parser.getRecords();

        for (int i=0; i < ruleList.size(); i++)
        {
            if (i==0) continue;

            CSVRecord record = ruleList.get(i);
            HCSConceptDescriptor conceptDescriptor = new SimpleHCSConceptDescriptor(record.get(0).trim(), record.get(4).trim(), record.get(5).trim());

            String ruleId = "SLS-R-" + String.format("%04d", i);
            if (! (conceptDescriptor.getSystem().equals("2.16.840.1.113883.6.88")
                    || conceptDescriptor.getSystem().equals("2.16.840.1.113883.6.1")))
                codeList.add(conceptDescriptor);
            addRule(new MockSLSRule(ruleId, record.get(2) + " " + record.get(3), conceptDescriptor, RESTRICTED_CODE));

        }



        LOGGER.log(Level.INFO, String.format("Loaded %d SLS rules.", ruleList.size()));
//        System.out.println("Loaded " + ruleList.size() + " SLS rules.");

    }

    public List<HCSConceptDescriptor> getCodeList() {
        return codeList;
    }

    public static void main(String[] args) throws IOException {
        CSVBasedRestrictedCodesSLSRulesDB rulesDB = new CSVBasedRestrictedCodesSLSRulesDB(new File("C:\\Moka\\temp\\esc-sls\\sls-impl\\src\\main\\resources\\sens-codes.csv"));
       // System.out.println();
    }
}