package gov.hhs.onc.leap.sls.data.data;

/**
 * ddecouteau@saperi.io
 */
public class SimpleHCSConceptDescriptor implements HCSConceptDescriptor {

//    public static String UMLS_SYSTEM = "umls.nlm.nih.gov";
//    public static String SNOMED = "2.16.840.1.113883.6.96";
//    public static String RxNORM = "2.16.840.1.113883.6.88";
//    public static String ICD9 = "2.16.840.1.113883.6.103";
//    public static HCSConceptDescriptor FHIR_RESTRICTED_CODE = new SimpleHCSConceptDescriptor("http://hl7.org/fhir/v3/Confidentiality", "R", "Restricted");



    String code;
    String system;
    String codeDisplay;
    
    public SimpleHCSConceptDescriptor() {
        
    }

    public SimpleHCSConceptDescriptor(String system, String code) {
        this.system=system;
        this.code = code;

    }
    public SimpleHCSConceptDescriptor(String system, String code, String codeDisplay) {
        this.system = system;
        this.code = code;
        this.codeDisplay = codeDisplay;
    }


    public String getCode() {
        return code;
    }

    public String getSystem() {
        return system;
    }

    public String getCodeDisplay() {
        return codeDisplay;
    }

    public String toString()
    {
        if (codeDisplay != null)
            return codeDisplay;
        else
            return system + "/" + code;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof HCSConceptDescriptor))
            return false;
        else
            return (this.getCode().equals(((HCSConceptDescriptor)obj).getCode()) && this.getSystem().equals(((HCSConceptDescriptor)obj).getSystem()));
    }
}