package gov.hhs.onc.leap.sls.text_analysis.lucene;

import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.StandardFilter;


import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;

/**
 * ddecouteau@saperi.io
 */
public class CustomAnalyzer extends StopwordAnalyzerBase {


    /** Default maximum allowed token length */
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

    /** An unmodifiable set containing some common English words that are usually not
     useful for searching. */
    public static final CharArraySet STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;

    /** Builds an analyzer with the given stop words.
     * @param stopWords stop words */
    public CustomAnalyzer(CharArraySet stopWords) {
        super(stopWords);
    }

    /** Builds an analyzer with the default stop words ({@link #STOP_WORDS_SET}).
     */
    public CustomAnalyzer() {
        this(STOP_WORDS_SET);
    }

    /** Builds an analyzer with the stop words from the given reader.
     * @see WordlistLoader#getWordSet(Reader)
     * @param stopwords Reader to read stop words from */
    public CustomAnalyzer(Reader stopwords) throws IOException {
        this(loadStopwordSet(stopwords));
    }

    /**
     * Set maximum allowed token length.  If a token is seen
     * that exceeds this length then it is discarded.  This
     * setting only takes effect the next time tokenStream or
     * tokenStream is called.
     */
    public void setMaxTokenLength(int length) {
        maxTokenLength = length;
    }

    /**
     * @see #setMaxTokenLength
     */
    public int getMaxTokenLength() {
        return maxTokenLength;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        final Tokenizer src;
        LetterTokenizer t = new LetterTokenizer();
        src = t;
        TokenStream tok = new StandardFilter(src);
        tok = new LengthFilter(tok, 2, 100);
        tok = new LowerCaseFilter(tok);
        tok = new StopFilter(tok, stopwords);
        return new TokenStreamComponents(src, tok) {
            @Override
            protected void setReader(final Reader reader) {
//                int m = CustomAnalyzer.this.maxTokenLength;
//                if (src instanceof LetterTokenizer) {
//                    ((LetterTokenizer)src).setMaxTokenLength(m);
//                } else {
//                    ((StandardTokenizer40)src).setMaxTokenLength(m);
//                }
                super.setReader(reader);
            }
        };
    }
}

