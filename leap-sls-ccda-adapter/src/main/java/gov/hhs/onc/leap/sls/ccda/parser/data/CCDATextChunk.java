package gov.hhs.onc.leap.sls.ccda.parser.data;

/**
 * ddecouteau@saperi.io
 */
class CCDATextChunk {
    String text;
    String metadata;

    CCDATextChunk (String text, String metadata)
    {
        this.text=text;
        this.metadata=metadata;
    }

    public String getMetadata() {
        return metadata;
    }

    public String getText() {
        return text;
    }
}
