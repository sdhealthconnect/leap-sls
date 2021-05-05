package gov.hhs.onc.leap.sls.data.data;

import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public interface HCSClinicalFact {

    /**
     * this returns an id for the clinical fact which is unique at least within the bundle.
     * @return
     */
    String getId();

    /**
     * this returns an optional textual representation of the clinical fact (e.g. the display name for immunization).
     * the text is used for making the logs more meaningful and readable.
     * @return
     */
    String toString();

    void addSecurityLabel(HCSConceptDescriptor securityLabel);

    /**
     * remove a security label from the clinical fact
     * @param securityLabel
     * @return false if the securityLabel was not found; true otherwise.
     */
    boolean removeSecurityLabel(HCSConceptDescriptor securityLabel);

    List<HCSConceptDescriptor> getSecurityLabels();

    List<HCSConceptDescriptor> getCodes();
}
