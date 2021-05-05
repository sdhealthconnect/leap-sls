package gov.hhs.onc.leap.sls.service.rest;

/**
 * ddecouteau@saperi.io
 */
import gov.hhs.onc.leap.service.msg.MsgSource;
import gov.hhs.onc.leap.sls.service.data.entity.LabelingResult;
import gov.hhs.onc.leap.sls.service.data.entity.NlpSlsTask;
import gov.hhs.onc.leap.sls.ccda.parser.CCDAParser;
import gov.hhs.onc.leap.sls.ccda.parser.data.CCDASimpleBundle;

import gov.hhs.onc.leap.sls.service.data.LabelingTask;
import gov.hhs.onc.leap.sls.service.repository.LabelingResultRepository;
import gov.hhs.onc.leap.sls.service.repository.NlpSlsTaskRepository;
import gov.hhs.onc.leap.sls.service.util.LabelingTaskToLabelingResult;
import gov.hhs.onc.leap.sls.service.config.ServiceConfigurations;
import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;
import gov.hhs.onc.leap.sls.impl.sls.MockSLS;
import gov.hhs.onc.leap.v2.parser.HL7V2Parser;
import gov.hhs.onc.leap.v2.parser.data.V2SimpleBundle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.dao.DataIntegrityViolationException;
import org.xml.sax.SAXException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/sls")
@Tag(name = "SLS-NLP-Controller", description = "API for consuming clinical data and determining its privacy sensitivities.")
@Slf4j
public class LabelingResultsService {

    private final static Logger LOGGER = Logger.getLogger(LabelingResultsService.class.getName());
    
    
    @Autowired
    private LabelingResultRepository labelingResultRepo;

    @Autowired
    private NlpSlsTaskRepository nlpTaskRepo;
    
    @Autowired
    private ServiceConfigurations serviceConfigurations;
    
    @Autowired 
    private LinkedBlockingQueue<String> nlpTaskQueue;
    
    @Autowired
    private MockSLS slsEngine;
    
    @Value("${nlp.enabled}")
    private boolean nlpEnabled;
    
    @Operation(summary = "SLS NLP Processing Queue.",
            description = "Access contents of Queue for those message awaiting processing")    
    @GetMapping(path = "/getQueuedTasks")
    public List<String> getQueuedTasks() {

        Iterator<String> iterator = nlpTaskQueue.iterator();
        List <String> list = new ArrayList<>();
        while (iterator.hasNext())
            list.add(iterator.next());

        return list;

    }

    @Operation(summary = "SLS NLP Processing Results by Type.",
            description = "Returns listing of SLS NLP processing results by Message Type")    
    @GetMapping(path = "/getResultsByMessageType")
    public List<LabelingResult> getResultsByMessageType(
                                    @RequestParam(required = true) MsgSource msgsource) throws UnsupportedEncodingException {

        LabelingResultRepository repository = labelingResultRepo;

        List<LabelingResult> allResults = repository.getLabelingResultByMessageType(msgsource.name());

        return allResults;
    }
    
    @Operation(summary = "SLS NLP Processing Results by Origin.",
            description = "Returns listing of SLS NLP processing results by their place of origin")    
    @GetMapping(path = "/getResultsByOrigin")
    public List<LabelingResult> getResultsByOrigin(
                                    @RequestParam(required = true) String origin) throws UnsupportedEncodingException {

        LabelingResultRepository repository = labelingResultRepo;

        List<LabelingResult> allResults = repository.getLabelingResultByOrigin(origin);

        return allResults;
    }
    
    @Operation(summary = "SLS NLP Processing Results by Id.",
            description = "Returns result of SLS NLP processing for a specific message.")    
    @GetMapping(path = "/getResultsById")
    public LabelingResult getResultsById(
            @RequestParam("id") String id) throws UnsupportedEncodingException {

        LabelingResultRepository repository =  labelingResultRepo;

        LabelingResult result = repository.getLabelingResultById(id);

        if (result == null)
            throw new WebApplicationException(404);

        return result;
    }

    @Operation(summary = "SLS NLP Processing Results by Id and Origin.",
            description = "Returns result of SLS NLP processing for a specific message.")    
    @GetMapping(path = "/getResultsByIdAndOrigin")
    public LabelingResult getResultsByIdAndOrigin(
            @RequestParam("id") String id,
            @RequestParam("origin") String origin) throws UnsupportedEncodingException {

        LabelingResultRepository repository =  labelingResultRepo;

        LabelingResult result = repository.getLabelingResultByIdAndOrigin(id, origin);

        if (result == null)
            throw new WebApplicationException(404);

        return result;
    }
    
    @Operation(summary = "Request SLS NLP Processing.",
            description = "Submit message for SLS NLP processing.")        
    @PostMapping(path = "/requestMessageProcessing",
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML}            
            )
    public LabelingResult requestMessageProcessing(
                    @RequestParam(required = true) String id,
                    @RequestParam(required = true) String origin,
                    @RequestParam(required = true) MsgSource msgSource,
                    @RequestParam(required = true) String msgVersion,
                    @RequestBody String content) {

        LOGGER.log(Level.INFO, String.format("Received a request for labeling document %s", id));

        if (labelingResultRepo.getLabelingResultById(id) != null) {
            LOGGER.log(Level.INFO, String.format("Duplicate document ID %s. Request rejected. " , id));
            throw new WebApplicationException
                    ("This document ID already exists.", Response.status(Response.Status.CONFLICT)
                    .build());
        }
        LabelingResult result = new LabelingResult();
        
        switch(msgSource) {
            case CCDA:
                return processCCDA(id, origin, msgVersion, content);
            case FHIR:
                return processFHIR(id, origin, msgVersion, content);
            case V2:
                return processV2(id, origin, msgVersion, content);
            default:
                throw new IllegalArgumentException("No source for " + msgSource); // should never-ever get here
        }

    }

    @Operation(summary = "Request SLS NLP Processing.",
            description = "Submit message for SLS NLP processing.")            
    @DeleteMapping(path = "/deleteProcessingResult")
    public Response deleteResult(
                @RequestParam("id") String id,
                @RequestParam("origin") String origin) {

        if (labelingResultRepo.getLabelingResultById(id) == null) {
            throw new WebApplicationException("'"+id+"' does not exist.", 404);
    
        } else {
            LabelingResult res = labelingResultRepo.getLabelingResultByIdAndOrigin(id, origin);
            labelingResultRepo.delete(res);    
        }
        
        return Response.ok().build();
    }
    
    private LabelingResult processCCDA(String id, String origin, String version, String content) {
        LabelingResult result = new LabelingResult();
        long startTime = System.currentTimeMillis();
        CCDASimpleBundle ccdaRecord = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes("UTF-8"));
            ccdaRecord = new CCDAParser().parseCodesFromInputStream(bais, id);

            LabelingTask task = new LabelingTask(ccdaRecord);
            task.addToProcessingTime(System.currentTimeMillis() - startTime);

            ArrayList<String> comments = new ArrayList<String>();

            startTime = System.currentTimeMillis();

            for (HCSClinicalFact fact: ccdaRecord.getClinicalFacts()) {
                slsEngine.labelClinicalFact(fact, comments);
                //trickle up the labels
                ccdaRecord.addSecurityLabels(fact.getSecurityLabels());
            }
            long elapsedTime = System.currentTimeMillis() - startTime;

            task.addComments(comments);
            task.addToProcessingTime(elapsedTime);


            if (ccdaRecord.getSecurityLabels().size() !=0 ) { //only send to NLP if there aren't any labels.
                result = LabelingTaskToLabelingResult.convertCompletedTask(task);
                try {
                    LOGGER.log(Level.INFO, String.format("Processing document ID %s is completed in %d milliseconds resulting in %d labels. Saving results. " , id, elapsedTime, ccdaRecord.getSecurityLabels().size()));
                    labelingResultRepo.save(result);
                }
                catch (DataIntegrityViolationException e)
                {
                    e.printStackTrace();
                    throw new WebApplicationException(e,409);
                }

            } else if (ccdaRecord.getSecurityLabels().size() == 0 && nlpEnabled) { //we are going to NLP
                LOGGER.log(Level.INFO, String.format("Processing document ID %s is completed in %d milliseconds. No sensitive codes detected in the structured parts of the documents. Sending the document to unstructured text processing. " , id, elapsedTime));
                task.addComment("No sensitive codes detected in the structured parts of the record. Sending the record for text analysis.");
                result =  LabelingTaskToLabelingResult.convertIncompletedTask(task);
                try {
                    labelingResultRepo.save(result);
                }
                catch (DataIntegrityViolationException e) {
                    e.printStackTrace();
                    throw new WebApplicationException(e, 409);
                }

                NlpSlsTask nlpTask = new NlpSlsTask();
                nlpTask.setId(id);
                nlpTask.setContent(content);
                nlpTaskRepo.save(nlpTask);
                nlpTaskQueue.add(nlpTask.getId());
            } else {
                LOGGER.log(Level.INFO, String.format("Processing document ID %s is completed in %d milliseconds. No sensitive codes detected in the structured parts of the documents. NOT Sending the document to unstructured text processing. " , id, elapsedTime));
                task.addComment("No sensitive codes detected in the structured parts of the record. Sending the record for text analysis.  NLP Disabled");
                result =  LabelingTaskToLabelingResult.convertCompletedTask(task);
                try {
                    labelingResultRepo.save(result);
                }
                catch (DataIntegrityViolationException e) {
                    e.printStackTrace();
                    throw new WebApplicationException(e, 409);
                }
            }

        } catch (IOException | XPathExpressionException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
            throw new WebApplicationException("Error in submitted CCDA: " + e, 400);
        }

        return result;
    }
    
    private LabelingResult processFHIR(String id, String origin, String version, String content) {
        LabelingResult result = new LabelingResult();
        
        return result;
    }
    
    private LabelingResult processV2(String id, String origin, String version, String content) {
            LabelingResult result = new LabelingResult();
            long startTime = System.currentTimeMillis();    
            HL7V2Parser v2Parser = new HL7V2Parser();
            try {
            V2SimpleBundle v2Record = v2Parser.parseMessage(content);
            LabelingTask task = new LabelingTask(v2Record);
            ArrayList<String> comments = new ArrayList<String>();

            startTime = System.currentTimeMillis();

            for (HCSClinicalFact fact: v2Record.getClinicalFacts()) {
                slsEngine.labelClinicalFact(fact, comments);
                //trickle up the labels
                v2Record.addSecurityLabels(fact.getSecurityLabels());
            }
            long elapsedTime = System.currentTimeMillis() - startTime;

            if (v2Record.getSecurityLabels().size() == 0) {
                comments.add("No sensitive codes detected in the structured parts of the record.");
            }
            
            task.addComments(comments);
            task.addToProcessingTime(elapsedTime);



                result = LabelingTaskToLabelingResult.convertCompletedTask(task);
                try {
                    LOGGER.log(Level.INFO, String.format("Processing document ID %s is completed in %d milliseconds resulting in %d labels. Saving results. " , id, elapsedTime, v2Record.getSecurityLabels().size()));
                    labelingResultRepo.save(result);
                }
                catch (DataIntegrityViolationException e)
                {
                    e.printStackTrace();
                    throw new WebApplicationException(e,409);
                }


        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException("Error in submitted CCDA: " + e, 400);
        }
        
        
        return result;
    }
}
