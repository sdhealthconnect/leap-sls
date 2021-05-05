package gov.hhs.onc.leap.service;

import gov.hhs.onc.leap.sls.ccda.parser.CCDAParser;
import gov.hhs.onc.leap.sls.ccda.parser.data.CCDASimpleBundle;
import gov.hhs.onc.leap.sls.ccda.parser.data.PlaintextClinicalFactAdapter;
import gov.hhs.onc.leap.sls.service.data.LabelingTask;
import gov.hhs.onc.leap.sls.service.data.entity.LabelingResult;
import gov.hhs.onc.leap.sls.service.data.entity.NlpSlsTask;
import gov.hhs.onc.leap.sls.service.repository.LabelingResultRepository;
import gov.hhs.onc.leap.sls.service.repository.NlpSlsTaskRepository;
import gov.hhs.onc.leap.sls.service.util.LabelingTaskToLabelingResult;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import gov.hhs.onc.leap.sls.data.sls.SLSInterface;
import gov.hhs.onc.leap.sls.impl.ruledb.CSVBasedRestrictedCodesSLSRulesDB;
import gov.hhs.onc.leap.sls.impl.sls.MockSLS;
import gov.hhs.onc.leap.sls.impl.sls.MockSLSRulesDB;
import gov.hhs.onc.leap.sls.text_analysis.lucene.TextAnalysisEngine;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * ddecouteau@saperi.io
 */


public class NlpSlsService extends Thread {

    private final static Logger LOGGER = Logger.getLogger(NlpSlsService.class.getName());


    private SLSInterface slsEngine = null;
    TextAnalysisEngine textAnalysisEngine = null;

    private NlpSlsTaskRepository nlpSlsTaskRepository;
    private LabelingResultRepository labelingResultRepository;

    private CCDAParser ccdaParser;

    private LinkedBlockingQueue<String> nlpTaskQueue;

    //private long pollingFrequencyInMilliSeconds = 2 * 1000;
    private boolean isShutdown = false;

    public NlpSlsService(MockSLSRulesDB slsRulesDB, LinkedBlockingQueue<String> nlpTaskQueue, NlpSlsTaskRepository nlpSlsTaskRepository, LabelingResultRepository labelingResultRepository) throws ParseException, IOException, ParserConfigurationException {

        slsEngine = new MockSLS(slsRulesDB);
        ccdaParser = new CCDAParser();
        textAnalysisEngine = new TextAnalysisEngine(((CSVBasedRestrictedCodesSLSRulesDB)slsRulesDB).getCodeList());
        this.nlpTaskQueue = nlpTaskQueue;
        this.nlpSlsTaskRepository = nlpSlsTaskRepository;
        this.labelingResultRepository = labelingResultRepository;
    }

   // public void setPollingFrequencyInMilliSeconds(long pollingFrequencyInMilliSeconds) {
        //this.pollingFrequencyInMilliSeconds = pollingFrequencyInMilliSeconds;
    //}

    public void shutdown()
    {
        isShutdown = true;
    }

    @Override
    public void run ()  {
        LOGGER.log(Level.INFO, String.format("NLP SLS thread %s started.", this.getName()));

        try {
            while (!isShutdown) {

                String taskId = nlpTaskQueue.take();
                NlpSlsTask nlpSlsTask = (NlpSlsTask)nlpSlsTaskRepository.getNlpSlsTaskById(taskId);

//                List<NlpSlsTask> nlpSlsTasks = nlpSlsTaskRepo.findAll();
//                if (nlpSlsTasks.size() == 0)
//                    sleep(pollingFrequencyInMilliSeconds);
//                else
//                    String ccdaContent = nlpSlsTasks.get(0).getContent();
//                    String ccdaId = nlpSlsTasks.get(0).getId();
                    String ccdaContent = nlpSlsTask.getContent();
                    String ccdaId = nlpSlsTask.getId();

                    LOGGER.log(Level.INFO, "Processing " + ccdaId);

                    try {
//                        CCDASimpleBundle ccdaRecord = ccdaParser.parseTextSectionsFromInputStream(new ByteArrayInputStream(ccdaContent.getBytes("UTF-8")), ccdaId);
                        CCDASimpleBundle ccdaRecord = ccdaParser.parseTextSectionsFromInputStreamIgnoreVAQuestionnaires(new ByteArrayInputStream(ccdaContent.getBytes("UTF-8")), ccdaId);
                        LabelingTask task = new LabelingTask(ccdaRecord);

                        List<String> comments = new ArrayList<String>();
                        comments.add("No sensitive codes detected in the structured parts of the record. Sending the record for text analysis.");

                        LOGGER.log(Level.INFO, String.format("%s parsed and %d text clinical facts extracted.", ccdaRecord.getId(), ccdaRecord.getPlainTextClinicalFacts().size() ));


                        long startTime = System.currentTimeMillis();
                        for (PlaintextClinicalFactAdapter plaintextClinicalFact : ccdaRecord.getPlainTextClinicalFacts()) {

                            LOGGER.log(Level.INFO, String.format("Processing %s (length: %d).", plaintextClinicalFact, plaintextClinicalFact.getText().length()));

                            List<HCSConceptDescriptor> clinicalCodes = textAnalysisEngine.processText(plaintextClinicalFact.getText());
                            plaintextClinicalFact.addClinicalCodes(clinicalCodes);
                            slsEngine.labelClinicalFact(plaintextClinicalFact, comments);
                            if (plaintextClinicalFact.getSecurityLabels().size() > 0) {
                                ccdaRecord.addSecurityLabels(plaintextClinicalFact.getSecurityLabels());
                                // TODO: uncomment if you want to process only up until a sensitive code is found.
                                // break;
                            }
                        }

                        long elapsedTime = System.currentTimeMillis() - startTime;

                        task.addComments(comments);
                        task.addToProcessingTime(elapsedTime);

                        LOGGER.log(Level.INFO, String.format("Record %s processed in %d milliseconds.", ccdaRecord.getId(), elapsedTime));


                        if (ccdaRecord.getSecurityLabels().size() == 0) {
                            LOGGER.log(Level.INFO, String.format("No security labels assigned to record %s.", ccdaRecord.getId()));
                        }
                        else
                        {
                            StringBuffer allLabels = new StringBuffer();
                            for (HCSConceptDescriptor securityLabel : ccdaRecord.getSecurityLabels())
                                allLabels.append(securityLabel.getCodeDisplay()).append(",");
                            allLabels.setLength(allLabels.length()-1); //drop the last comma
                            LOGGER.log(Level.INFO, String.format("Security labels assigned to record %s : %s.", ccdaRecord.getId(), allLabels.toString()));
                        }

                        LabelingResult labelingResult = LabelingTaskToLabelingResult.convertCompletedTask(task);
                        labelingResult.setIsNew(false);

                        labelingResultRepository.save(labelingResult);
                        NlpSlsTask sTask = nlpSlsTaskRepository.getNlpSlsTaskById(ccdaId);
                        nlpSlsTaskRepository.delete(sTask);

                    } catch ( IOException | XPathExpressionException | SAXException | InvalidTokenOffsetsException | ParseException  e) {
                        LOGGER.log(Level.INFO, String.format("An error occurred while processing %s: %s: %s", ccdaId, e.getClass().getCanonicalName(), e.getMessage()));
                        e.printStackTrace();
                        //System.out.println("The NLP service will continue processing. " );
                    }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
