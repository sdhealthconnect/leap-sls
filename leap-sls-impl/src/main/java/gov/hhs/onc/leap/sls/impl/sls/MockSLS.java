package gov.hhs.onc.leap.sls.impl.sls;

import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import gov.hhs.onc.leap.sls.data.sls.SLSInterface;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

/**
 * ddecouteau@saperi.io
 */
@Component
public class MockSLS implements SLSInterface {

    private final static Logger LOGGER = Logger.getLogger(MockSLS.class.getName());

    MockSLSRulesDB ruleDB;

    @Autowired
    public MockSLS ( MockSLSRulesDB ruleDB) {
        this.ruleDB = ruleDB;
    }


    public HCSClinicalFact labelClinicalFact(HCSClinicalFact clinicalFact, List<String> notes) {

        //notes.add("Invoking Security Labeling Service for clinical fact " + clinicalFact.toString()) ;

        for (HCSConceptDescriptor code : clinicalFact.getCodes())
        {
            MockSLSRule rule = ruleDB.getRule(code);
            if (rule != null)
            {
                notes.add("SLS rule " + rule.toString() + " matched code '" + code.toString()  + "' of " + clinicalFact.toString());
                LOGGER.log(Level.INFO, "SLS rule " + rule.toString() + " matched code '" + code.toString()  + "' of " + clinicalFact.toString());

                for (HCSConceptDescriptor label : rule.getTags())
                    clinicalFact.addSecurityLabel(label);
            }
        }
        return clinicalFact;
    }
}
