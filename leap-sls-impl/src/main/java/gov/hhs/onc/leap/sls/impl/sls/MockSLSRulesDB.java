package gov.hhs.onc.leap.sls.impl.sls;


import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import gov.hhs.onc.leap.sls.impl.common.MockHCSRulesDB;

/**
 * ddecouteau@saperi.io
 */
public class MockSLSRulesDB extends MockHCSRulesDB {
    public MockSLSRule getRule(HCSConceptDescriptor clinicalFact)
    {
        return (MockSLSRule) super.getRule(clinicalFact);
    }

}
