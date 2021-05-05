package gov.hhs.onc.leap.sls.data.data;

import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public interface HCSBundle {
    /**
     * adds a bundle-level security label
     * @param securityLabel
     */
    void addSecurityLabel(HCSConceptDescriptor securityLabel);

    /**
     * remove a bundle-level security label
     * @param securityLabel
     */
    boolean removeSecurityLabel(HCSConceptDescriptor securityLabel);

    /**
     * returns bundle-level security labels
     * @return
     */
    List<HCSConceptDescriptor> getSecurityLabels();

    List<HCSClinicalFact> getClinicalFacts();

    /**
     * delete a clinical fact from the bundle
     */
    void deleteClinicalFact (String id);

    /**
     * this returns an id for the clinical fact which is unique at least within the bundle.
     * @return
     */
    String getId();
}
