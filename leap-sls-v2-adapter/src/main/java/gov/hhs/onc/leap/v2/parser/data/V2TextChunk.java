package gov.hhs.onc.leap.v2.parser.data;

/**
 * ddecouteau@saperi.io
 */
class V2TextChunk {
    String text;
    String metadata;

    V2TextChunk (String text, String metadata)
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
