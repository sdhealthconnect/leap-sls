package gov.hhs.onc.leap.sls.impl.common;


import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 * ddecouteau@saperi.io
 */
public abstract class MockHCSRulesDB {
    Map<String,MockHCSRule> index = new HashMap<String, MockHCSRule>();

    public void addRule (MockHCSRule rule) {
        index.put(rule.getIndexKey(), rule);
    }

    public MockHCSRule getRule(HCSConceptDescriptor clinicalFact)
    {
        return index.get(clinicalFact.getSystem()+"/"+clinicalFact.getCode());
    }

}
