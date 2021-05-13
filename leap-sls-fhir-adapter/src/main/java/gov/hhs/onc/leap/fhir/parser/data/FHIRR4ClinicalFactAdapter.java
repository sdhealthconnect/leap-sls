package gov.hhs.onc.leap.fhir.parser.data;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import gov.hhs.onc.leap.sls.data.data.SimpleHCSConceptDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by VHA Development Staff - Questions may be directed to Mike Davis, Security and Privacy Architect - 20/04/2016.
 */
public class FHIRR4ClinicalFactAdapter implements HCSClinicalFact {

    //this might be questionable for multi-threading.
//    static FHIRCodeParser fhirCodeParser = null;
    FhirContext fhirContext = FhirContext.forR4();

    Resource myFHIRResource;
    Meta resourceMetadata;

    public FHIRR4ClinicalFactAdapter(Resource resource) {

//        if (fhirCodeParser == null) {
//            try {
//                fhirCodeParser = new FHIRCodeParser();
//            } catch (ParserConfigurationException e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            }
//        }

        myFHIRResource = resource;
        try {
            resourceMetadata = myFHIRResource.getMeta();
        }
        catch (Exception ex) {
            resourceMetadata = new Meta();
        }
    }

    public Resource getMyFHIRResource() {
        return myFHIRResource;
    }

    public String getId() {
        return myFHIRResource.getId().toString();
    }

    public void addSecurityLabel(HCSConceptDescriptor securityLabel) {
        Coding coding = new Coding();
        coding.setSystem(securityLabel.getSystem());
        coding.setCode(securityLabel.getCode());
        coding.setDisplay(securityLabel.getCodeDisplay());
        resourceMetadata.addSecurity(coding);
        Extension ext = new Extension();
        ext.setUrl("http://hl7.org/fhir/uv/security-label-ds4p/StructureDefinition/extension-sec-label-related-artifact");
        RelatedArtifact relatedArtifact = new RelatedArtifact();
        relatedArtifact.setType(RelatedArtifact.RelatedArtifactType.DOCUMENTATION);
        relatedArtifact.setUrl("http://example.fhir.org/base/Provenance/some-provenance");
        resourceMetadata.addExtension(ext);
        myFHIRResource.setMeta(resourceMetadata);
    }

    public boolean removeSecurityLabel(HCSConceptDescriptor hcsConceptDescriptor) {
        //resourceMetadata.
        //todo: not supported because no support in HAPI FHIR
        return false;
    }

    public List<HCSConceptDescriptor> getSecurityLabels() {

        List<HCSConceptDescriptor> securityLabels = new ArrayList<HCSConceptDescriptor>();

        for (Coding coding : resourceMetadata.getSecurity())
            securityLabels.add(new SimpleHCSConceptDescriptor(coding.getSystem(), coding.getCode(), coding.getDisplay()));

        return securityLabels;
    }

    public List<HCSConceptDescriptor> getCodes() {
        List<Coding> codings = fhirContext.newTerser().getAllPopulatedChildElementsOfType(myFHIRResource, Coding.class);
        //System.out.println("CODING ARRAY SIZE" + codings.size());
        HashSet<HCSConceptDescriptor> codesSet = new HashSet<HCSConceptDescriptor>();
        for (Coding coding : codings) {
            String system = StringUtils.trimToEmpty(coding.getSystem());
            String code = StringUtils.trimToEmpty(coding.getCode());
            String display = StringUtils.trimToEmpty(coding.getDisplay());
            System.out.println(code +" "+system+" "+display);
            if (!"".equals(system) && !"".equals(code))
                codesSet.add(new SimpleHCSConceptDescriptor(system, code, display));
        }
        return Arrays.<HCSConceptDescriptor>asList(codesSet.toArray(new HCSConceptDescriptor[0]));
    }


    public String toString()
    {
        return myFHIRResource.getId();
    }


    public static void main(String[] args) throws Exception {
        FhirContext fhirContext = FhirContext.forR4();


        String serverBase = "http://34.94.253.50:8080/hapi-fhir-jpaserver/fhir";

        String uri = "http://34.94.253.50:8080/hapi-fhir-jpaserver/fhir/Patient/2035/$everything";
//
        IGenericClient client = fhirContext.newRestfulGenericClient(serverBase);
//
        Bundle bundle = client.search().byUrl(uri).returnBundle(Bundle.class).execute();

        for (Bundle.BundleEntryComponent entry : bundle.getEntry())
        {
            Resource resource = entry.getResource();
            FHIRR4ClinicalFactAdapter clinicalFactAdapter = new FHIRR4ClinicalFactAdapter(resource);
            
            List<HCSConceptDescriptor> codes = clinicalFactAdapter.getCodes();
            System.out.println();
        }



//
        //FHIRDstu3ClinicalFactAdapter clinicalFactAdapter = new FHIRDstu3ClinicalFactAdapter(resource);
//        clinicalFactAdapter.addSecurityLabel(new ConceptDescriptorAdapter("test","test","this is a test"));
//        clinicalFactAdapter.addSecurityLabel(new ConceptDescriptorAdapter("test2","test2","this is a second test"));
//


//
        System.out.println();
    }
}
