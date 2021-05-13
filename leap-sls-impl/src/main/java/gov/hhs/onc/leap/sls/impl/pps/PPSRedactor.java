package gov.hhs.onc.leap.sls.impl.pps;


import gov.hhs.onc.leap.sls.data.data.HCSBundle;
import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import gov.hhs.onc.leap.sls.data.pps.PPSActor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ddecouteau@saperi.io
 */
public class PPSRedactor implements PPSActor {
    private final static Logger LOGGER = Logger.getLogger(PPSRedactor.class.getName());

    HCSConceptDescriptor redactionCode;

//    static final String REDACT_CODE_SYSTEM="http://hl7.org/fhir/v3/ObservationValue";
//    static final String REDACT_CODE_NAME="REDACTED";

    public PPSRedactor (HCSConceptDescriptor redactionCode) {
        this.redactionCode = redactionCode;
    }

    public HCSBundle apply(HCSBundle bundle, List<String> notes) {
        List<HCSClinicalFact> toBeDeleted = new ArrayList<HCSClinicalFact>();
        List<HCSClinicalFact> clinicalFacts = bundle.getClinicalFacts();

        //find all the clinical facts that bear the redaction code
        for (HCSClinicalFact fact: clinicalFacts)
        {
            List<HCSConceptDescriptor> securityLabels = fact.getSecurityLabels();
            for (HCSConceptDescriptor label : securityLabels)
            {
                if (redactionCode.getSystem().equals(label.getSystem()) && redactionCode.getCode().equals(label.getCode())) {
                    notes.add("Redacting " + fact.toString());
                    LOGGER.log(Level.INFO, "Redacting " + fact.toString() + " from " + bundle.getId());
                    toBeDeleted.add(fact);
                }
            }
        }

        //remove all the clinical facts that bear the redaction code
        for (HCSClinicalFact toBeDeletedFact : toBeDeleted)
            bundle.deleteClinicalFact(toBeDeletedFact.getId());

        return bundle;
    }
}
