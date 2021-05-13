package gov.hhs.onc.leap.sls.impl.pps;


import gov.hhs.onc.leap.sls.data.data.HCSBundle;
import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import gov.hhs.onc.leap.sls.data.pps.PPSActor;
import gov.hhs.onc.leap.sls.data.pps.PPSInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ddecouteau@saperi.io
 */
public class MockPPS implements PPSInterface {

    private final static Logger LOGGER = Logger.getLogger(MockPPS.class.getName());


    MockPPSRulesDB ruleDB = new MockPPSRulesDB();
    List<PPSActor> ppsActors = new ArrayList<PPSActor>();

    public MockPPS (MockPPSRulesDB ruleDB)
    {
        this.ruleDB = ruleDB;
    }

    public void addActor(PPSActor actor) {
        ppsActors.add(actor);
    }

    public HCSClinicalFact mark(HCSClinicalFact clinicalFact, List<String> notes) {
        LOGGER.log(Level.INFO, "Invoking Privacy Preserving Services for clinical fact " + clinicalFact.toString() );
        notes.add("Invoking Privacy Preserving Services for clinical fact " + clinicalFact.toString()) ;
        List<HCSConceptDescriptor> oldSecurityLabels = clinicalFact.getSecurityLabels();

        for (HCSConceptDescriptor label : oldSecurityLabels)
        {
            MockPPSRule rule = ruleDB.getRule(label);
            if (rule != null)
            {
                notes.add("Applying PPS rule " + rule.toString() + " to " + clinicalFact.toString());
                LOGGER.log(Level.INFO, "Applying PPS rule " + rule.toString() + " to " + clinicalFact.toString() );
                for (HCSConceptDescriptor ppsLabel : rule.getTags()) {
                    clinicalFact.addSecurityLabel(ppsLabel);
                }
            }
        }

        for (HCSConceptDescriptor label : oldSecurityLabels)
            clinicalFact.removeSecurityLabel(label);

        return clinicalFact;
    }



    public HCSBundle fulfil(HCSBundle bundle, List<String> notes) {

        for (PPSActor action : ppsActors)
            action.apply(bundle, notes);

        return bundle;
    }
}
