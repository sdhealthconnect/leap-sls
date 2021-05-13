package gov.hhs.onc.leap.fhir.parser.data;

import gov.hhs.onc.leap.sls.data.data.HCSBundle;
import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by VHA Development Staff - Questions may be directed to Mike Davis, Security and Privacy Architect - 20/04/2016.
 */
public class FHIRR4BundleAdapter implements HCSBundle {
    Bundle myFHIRBundle;

    public FHIRR4BundleAdapter(Bundle bundle)
    {
        myFHIRBundle = bundle;
    }

    public Bundle getMyFHIRBundle()
    {
        return myFHIRBundle;
    }

    public void addSecurityLabel(HCSConceptDescriptor securityLabel) {

        Coding coding = new Coding();
        coding.setSystem(securityLabel.getSystem());
        coding.setCode(securityLabel.getCode());
        coding.setDisplay(securityLabel.getCodeDisplay());

        myFHIRBundle.getMeta().addSecurity(coding);
    }

    public boolean removeSecurityLabel(HCSConceptDescriptor securityLabel) {
        return false;
    }

    public List<HCSConceptDescriptor> getSecurityLabels()
    {
        //todo:  not supported
        return new ArrayList<HCSConceptDescriptor>();
    }

    /**
     * This method creates a list from scratch every time it's called so it's expensive to call. Save the result and re-use instead of re-calling it.
     * @return
     */
    public List<HCSClinicalFact> getClinicalFacts() {
        List<HCSClinicalFact> clinicalFacts = new ArrayList<HCSClinicalFact>();
        for (Bundle.BundleEntryComponent entry: myFHIRBundle.getEntry()) {
            Resource resource = entry.getResource();
            clinicalFacts.add(new FHIRR4ClinicalFactAdapter(resource));
        }
        return clinicalFacts;
    }

    public void deleteClinicalFact(String id) {

        Iterator<Bundle.BundleEntryComponent> entryIterator = myFHIRBundle.getEntry().iterator();
        while (entryIterator.hasNext())
        {
            if (entryIterator.next().getResource().getId().equals(id)) {
                entryIterator.remove();
                break;
            }
        }
    }

    public String getId() {
        return "";
    }
}
