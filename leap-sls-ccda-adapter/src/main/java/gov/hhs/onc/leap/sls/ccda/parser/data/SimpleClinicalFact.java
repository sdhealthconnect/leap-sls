package gov.hhs.onc.leap.sls.ccda.parser.data;



import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public class SimpleClinicalFact implements HCSClinicalFact {
    String id;

    List<HCSConceptDescriptor> codes = new ArrayList<HCSConceptDescriptor>();
    List<HCSConceptDescriptor> securityLabels = new ArrayList<HCSConceptDescriptor>();

    String metadata;


    public SimpleClinicalFact(String id, String metadata, HCSConceptDescriptor code)
    {
        this.metadata = metadata;
        this.id = id;
        this.codes.add(code);
    }

    public String getId() {
        return id;
    }

    public void addSecurityLabel(HCSConceptDescriptor hcsConceptDescriptor) {
        if (! securityLabels.contains(hcsConceptDescriptor))
            securityLabels.add(hcsConceptDescriptor);
    }

    public boolean removeSecurityLabel(HCSConceptDescriptor hcsConceptDescriptor) {
        //not implemented at this point because we won't need it.
        return false;
    }

    public List<HCSConceptDescriptor> getSecurityLabels() {
        return securityLabels;
    }

    public List<HCSConceptDescriptor> getCodes() {
        return codes;
    }



    public String getMetadata() {
        return metadata;
    }

    public String toString()
    {
        return id + "(" + metadata + ")";
    }

}
