package gov.hhs.onc.leap.sls.impl.sls;



import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import gov.hhs.onc.leap.sls.impl.common.MockHCSRule;

import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public class MockSLSRule extends MockHCSRule {

    public MockSLSRule(String id, String comments, HCSConceptDescriptor clinicalFact, List<HCSConceptDescriptor> tags) {
        super(id, comments, clinicalFact, tags);
    }

    public MockSLSRule(String id, String comments, HCSConceptDescriptor clinicalFact, HCSConceptDescriptor[] tags) {
        super(id, comments, clinicalFact, tags);
    }
}
