package gov.hhs.onc.leap.sls.impl.pps;


import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import gov.hhs.onc.leap.sls.impl.common.MockHCSRulesDB;

/**
 * ddecouteau@saperi.io
 */
public class MockPPSRulesDB extends MockHCSRulesDB {

    public MockPPSRule getRule(HCSConceptDescriptor clinicalFact)
    {
        return (MockPPSRule) super.getRule(clinicalFact);
    }
}
