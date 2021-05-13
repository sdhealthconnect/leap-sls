package gov.hhs.onc.leap.sls.ccda.parser;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public class TextChunker {

    private int chunkSize = 2*1024; //2KB

    public TextChunker()
    { }
    public TextChunker(int chunkSize)
    {
        this.chunkSize = chunkSize;
    }

    public  List<String> getChunks (String text)
    {
        List<String> chunks = new ArrayList<>();

        text = CharMatcher.whitespace().collapseFrom(text, ' ');
        Iterable<String> lines = Splitter.on(". ").omitEmptyStrings().split(text);
        String nextString="";
        for (String line : lines)
        {
            if (nextString.length() > chunkSize) {
                nextString = nextString + line + ". ";
                chunks.add(nextString);
                nextString = "";
            }
            else
                nextString = nextString + line + ". ";
        }
        if (!"".equals(nextString))
            chunks.add(nextString);

        return chunks;
    }
}
