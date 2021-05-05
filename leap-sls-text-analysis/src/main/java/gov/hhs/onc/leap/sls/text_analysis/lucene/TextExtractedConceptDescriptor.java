package gov.hhs.onc.leap.sls.text_analysis.lucene;


import gov.hhs.onc.leap.sls.data.data.SimpleHCSConceptDescriptor;

/**
 * ddecouteau@saperi.io
 */
public class TextExtractedConceptDescriptor extends SimpleHCSConceptDescriptor {

    String phrase;


    public TextExtractedConceptDescriptor(String system, String code, String phrase) {
        super(system, code);
        this.phrase=phrase;
    }

    public TextExtractedConceptDescriptor(String system, String code, String codeDisplay, String phrase) {
        super(system, code, codeDisplay);
        this.phrase = phrase;
    }

    public String toString()
    {
        return super.toString() + " ['" + phrase + "']";
    }
}
