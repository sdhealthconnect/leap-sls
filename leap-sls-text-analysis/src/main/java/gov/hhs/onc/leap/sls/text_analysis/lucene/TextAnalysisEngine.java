package gov.hhs.onc.leap.sls.text_analysis.lucene;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;

import java.io.IOException;
import java.util.*;

/**
 * ddecouteau@saperi.io
 */
public class TextAnalysisEngine {
    private static String CONTENT_FIELD_NAME = "CONTENT";
    private static float SENSITIVITY_THRESHOLD =0.0099f;

    Analyzer analyzer;
    QueryParser parser;
    //List<HCSConceptDescriptor> codeList;
    Map<Query, HCSConceptDescriptor> queryToCodeMap = new HashMap<>();
    List<Query> queryList;




    public TextAnalysisEngine(List<HCSConceptDescriptor> codeList) throws ParseException, IOException {
//        analyzer = new SimpleAnalyzer();
//        CharArraySet stopSet = CharArraySet.copy(StandardAnalyzer.STOP_WORDS_SET);
        CharArraySet stopSet = new CharArraySet(30, true);
        char[] theNo ={'n','o'};
        char[] theNot ={'n','o','t'};
        char[] theNon ={'n','o','n'};

        Object [] stopWords = StandardAnalyzer.STOP_WORDS_SET.toArray();
        for (Object word : stopWords)
        {
            char[] theWord = (char[]) word;

            if (Arrays.equals(theNo, theWord)
                    || Arrays.equals(theNot, theWord)
                    || Arrays.equals(theNon, theWord))
                continue;
            stopSet.add(word);

        }

//        stopSet.remove("no");
//        stopSet.remove("not");
//        stopSet.remove("non");
        stopSet.add("other");
        stopSet.add("another");
        stopSet.add("use");
        stopSet.add("any");
        stopSet.add("type");
        stopSet.add("combination");
        stopSet.add("plan");
        stopSet.add("care");
        stopSet.add("history");
        stopSet.add("mixed");
//        stopSet.add("oral");
//        stopSet.add("stool");
//        stopSet.add("capsule");
//        stopSet.add("liquid");
//        stopSet.add("cream");
//        stopSet.add("solution");
//        stopSet.add("sleep");

        stopSet.add("mg");
        stopSet.add("ml");
        stopSet.add("hr");
        stopSet.add("ds");
        stopSet.add("eg");
        stopSet.add("am");
        stopSet.add("pm");
        stopSet.add("cd");
        stopSet.add("ur");

        stopSet.add("identified");
        stopSet.add("patient");
//        stopSet.add("screen");
        stopSet.add("ratio");
//        stopSet.add("tablet");
        stopSet.add("disorder");
        stopSet.add("specified");
        stopSet.add("unspecified");
        stopSet.add("time");
//        stopSet.add("topical");
        stopSet.add("unknown");
        stopSet.add("free");
        stopSet.add("XXX");
//        stopSet.add("tissue");
        stopSet.add("mass");
        stopSet.add("product");
//        analyzer = new ClassicAnalyzer(stopSet);
        analyzer = new CustomAnalyzer(stopSet);


//        analyzer = new EnglishAnalyzer(stopSet);
        parser = new QueryParser(CONTENT_FIELD_NAME, analyzer);

        for (HCSConceptDescriptor code : codeList)
        {
            String text = code.getCodeDisplay();
            Query q = prepareQuery(text);

            if (q==null)
                continue;

            queryToCodeMap.put(q, code);
        }
        queryList = new ArrayList<>(queryToCodeMap.keySet());
//        System.out.println();

    }

    private Query prepareQuery(String text) throws IOException, ParseException {
        //clean up the query text
        text = StringUtils.trimToEmpty(text);


        text = StringUtils.lowerCase(text);
        text = StringUtils.replace(text, "/", " ");
        text = StringUtils.replace(text, ";", " ");
        text = StringUtils.replace(text, ",", " ");
        text = StringUtils.replace(text, "(", " ");
        text = StringUtils.replace(text, ")", " ");
        text = StringUtils.replace(text, "[", " ");
        text = StringUtils.replace(text, "]", " ");
        text = StringUtils.replace(text, "{", " ");
        text = StringUtils.replace(text, "}", " ");
        text = StringUtils.replace(text, "\"", " ");
        text = StringUtils.replace(text, "+", " ");
        text = StringUtils.replace(text, "-", " ");
        text = StringUtils.replace(text, "|", " ");


//        {
        Iterable<String> words = Splitter.on(" ").omitEmptyStrings().split(text);
        text = Joiner.on(" ").skipNulls().join(words);

        if (StringUtils.countMatches(text, " ") > 5
                || StringUtils.contains(text, "alcohol")
                || StringUtils.contains(text, "aids")
                || StringUtils.contains(text, "drug")
                || StringUtils.contains(text, "stool")
                || StringUtils.contains(text, "dna")
                || StringUtils.contains(text, "abuse")
                || StringUtils.contains(text, "exposure")
                || StringUtils.contains(text, "serum")
                || StringUtils.contains(text, "plasma")
                || StringUtils.contains(text, "blood")
                || StringUtils.contains(text, "infection")
                || StringUtils.contains(text, "urine")
                || StringUtils.contains(text, "acute")
                || StringUtils.contains(text, "antibody")
                || StringUtils.contains(text, "infection")
                )
        {
            words = Splitter.on(" ").omitEmptyStrings().split(text);
            text = Joiner.on(" +").skipNulls().join(words);
            text = "+" + text;

        }
        else {

            words = Splitter.on(" ").omitEmptyStrings().split(text);
            int termCount = 1;
            Iterator<String> iterator = words.iterator();
            while (iterator.hasNext()) {
                String term = iterator.next();
                if (termCount == 1)
                    text = term + "^3";
                else
                    text = text + " " + term;
                termCount++;
            }
        }


        if (text.isEmpty())
            return null;

        Query query = parser.parse(text);


//        TokenStream stream = analyzer.tokenStream(CONTENT_FIELD_NAME, new StringReader(text));
//        CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
//        stream.reset();
//
//
//        List<SpanTermQuery> clauses = new ArrayList<>();
//
//        int termCount = 0;
//
//        while (stream.incrementToken()) {
//            Term term = new Term(CONTENT_FIELD_NAME, cattr.toString());
//            clauses.add(new SpanTermQuery(term));
//            termCount++;
//        }
//        stream.end();
//        stream.close();
//
//        if (termCount <= 1)
//            query = clauses.get(0);
//        else
//            query = new SpanNearQuery(clauses.toArray(new SpanQuery[clauses.size()]), 5, false);


//        System.out.println();

//            }
//                text = "\"" + text +"\"~15";
//                text = text + "-no -not -negative -deny -non";
//        }


        return query;

    }


    public List<HCSConceptDescriptor> processText (String text) throws ParseException, IOException, InvalidTokenOffsetsException {
        List<HCSConceptDescriptor> codes = new ArrayList<HCSConceptDescriptor>();



        MemoryIndex index = new MemoryIndex();

        index.addField(CONTENT_FIELD_NAME, text, analyzer);

        for (Query query : queryList) {
            //Query query = parser.parse(queryString);
            QueryScorer scorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(scorer);
            float score = index.search(query);
            String bestFragment = highlighter.getBestFragment(analyzer, CONTENT_FIELD_NAME, text);

            if (score > SENSITIVITY_THRESHOLD) {
                HCSConceptDescriptor code = queryToCodeMap.get(query);
                TextExtractedConceptDescriptor textExtractedConceptDescriptor = new TextExtractedConceptDescriptor(code.getSystem(), code.getCode(),code.getCodeDisplay(), bestFragment);
                codes.add(textExtractedConceptDescriptor);
//                System.out.println("\t" + code + ": " + score*100 );
//                System.out.println("\t\t '" + bestFragment + "'");
            }
        }


        return codes;
    }
}
