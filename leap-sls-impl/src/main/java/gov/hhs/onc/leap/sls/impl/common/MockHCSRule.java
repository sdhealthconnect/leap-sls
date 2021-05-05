package gov.hhs.onc.leap.sls.impl.common;


import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;

import java.util.Arrays;
import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public abstract class MockHCSRule {
    String id;

    String comments;

    HCSConceptDescriptor clinicalFact;

    List<HCSConceptDescriptor> tags;
    public String getIndexKey()
    {
        return clinicalFact.getSystem()+"/"+clinicalFact.getCode();
    }

    public MockHCSRule (String id, String comments, HCSConceptDescriptor clinicalFact, List<HCSConceptDescriptor> tags)
    {
        this.id = id;
        this.comments = comments;
        this.clinicalFact = clinicalFact;
        this.tags = tags;
    }

    public MockHCSRule (String id, String comments, HCSConceptDescriptor clinicalFact, HCSConceptDescriptor[] tags)
    {
        this.id = id;
        this.comments = comments;
        this.clinicalFact = clinicalFact;
        this.tags = Arrays.asList(tags);
    }

    public String getId() {
        return id;
    }

    public String toString()
    {
        return id + " (" + comments + ")";
    }


    public HCSConceptDescriptor getClinicalFact() {
        return clinicalFact;
    }

    public String getComments() {
        return comments;
    }

    public List<HCSConceptDescriptor> getTags() {
        return tags;
    }
}
