package gov.hhs.onc.leap.sls.ccda.parser.data;

import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public class PlaintextClinicalFactAdapter implements HCSClinicalFact {

    String id;
    String text;
    String metadata;
    List<HCSConceptDescriptor> codes = new ArrayList<>();
    List<HCSConceptDescriptor> securityLabels = new ArrayList<>();



    public PlaintextClinicalFactAdapter(String id, String text, String metadata)
    {
        this.text = text;
        this.metadata = metadata;
        this.id = id;
    }



    @Override
    public String getId() {
        return id;
    }

    @Override
    public void addSecurityLabel(HCSConceptDescriptor hcsConceptDescriptor) {
        if (! securityLabels.contains(hcsConceptDescriptor))
            securityLabels.add(hcsConceptDescriptor);
    }

    @Override
    public boolean removeSecurityLabel(HCSConceptDescriptor hcsConceptDescriptor) {
        //not implemented at this point because we won't need it.
        return false;
    }

    @Override
    public List<HCSConceptDescriptor> getSecurityLabels() {
        return securityLabels;
    }

    @Override
    public List<HCSConceptDescriptor> getCodes() {
        return codes;
    }

    public void addClinicalCode(HCSConceptDescriptor clinicalCode)
    {
        if (! codes.contains(clinicalCode))
            codes.add(clinicalCode);
    }

    public void addClinicalCodes(List<HCSConceptDescriptor> clinicalCodes)
    {
        for (HCSConceptDescriptor code: clinicalCodes)
            addClinicalCode(code);
    }

    public String getText() {
        return text;
    }

    public String getMetadata() {return metadata;}

    @Override
    public String toString()
    {
        return id +"(" + metadata + ")";
    }
}
