package gov.hhs.onc.leap.sls.data.pps;


import gov.hhs.onc.leap.sls.data.data.HCSBundle;
import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;

import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public interface PPSInterface {

    void addActor(PPSActor actor);

    /**
     * Add labels to the clinical fact denoting the PPS action that must be applied to it
     * @param fact
     * @param notes
     * @return
     */
    HCSClinicalFact mark (HCSClinicalFact fact, List<String> notes);

    /**
     * Iterates over the clinical facts in a bundle and invoked the corresponding actions (e.g. redaction) accordingly.
     * @param bundle
     * @param notes
     * @return
     */
    HCSBundle fulfil (HCSBundle bundle, List<String> notes);
}
