package gov.hhs.onc.leap.sls.ccda.parser.data;



import gov.hhs.onc.leap.sls.data.data.HCSBundle;
import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * ddecouteau@saperi.io
 */

public class CCDASimpleBundle implements HCSBundle {

    String id;
    List<HCSConceptDescriptor> securityLabels = new ArrayList<HCSConceptDescriptor>();
    List<HCSClinicalFact> clinicalFacts = new ArrayList<HCSClinicalFact>();

    List<PlaintextClinicalFactAdapter> plainTextClinicalFacts = new ArrayList<PlaintextClinicalFactAdapter>();


    public CCDASimpleBundle(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void addPlainTextClinicalFact (PlaintextClinicalFactAdapter plaintextClinicalFactAdapter)
    {
        plainTextClinicalFacts.add(plaintextClinicalFactAdapter);
    }

    public List<PlaintextClinicalFactAdapter> getPlainTextClinicalFacts() {
        return plainTextClinicalFacts;
    }

    public void addClinicalFact (HCSClinicalFact clinicalFact)
    {
        if (! clinicalFacts.contains(clinicalFact))
            clinicalFacts.add(clinicalFact);
    }

    public void addClinicalFacts (List<HCSClinicalFact> clinicalFacts)
    {
        clinicalFacts.addAll(clinicalFacts);
    }

    public void addSecurityLabels (List<HCSConceptDescriptor> securityLabels) {
        for (HCSConceptDescriptor label: securityLabels)
            addSecurityLabel(label);
    }
    public void addSecurityLabel (HCSConceptDescriptor securityLabel) {
        if (!securityLabels.contains(securityLabel))
            securityLabels.add(securityLabel);
    }

    public boolean removeSecurityLabel (HCSConceptDescriptor hcsConceptDescriptor) {
        //don't care for now
        return true;
    }

    public List<HCSConceptDescriptor> getSecurityLabels() {
        return securityLabels;
    }

    public List<HCSClinicalFact> getClinicalFacts() {
        return clinicalFacts;
    }

    public void deleteClinicalFact (String s) {
        //don't care for now
    }
}
