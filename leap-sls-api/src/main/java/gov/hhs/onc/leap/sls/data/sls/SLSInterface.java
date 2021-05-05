package gov.hhs.onc.leap.sls.data.sls;


import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;

import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public interface SLSInterface {

    HCSClinicalFact labelClinicalFact (HCSClinicalFact clinicalFact, List<String> notes);
}
