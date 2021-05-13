package gov.hhs.onc.leap.sls.service.config;


import gov.hhs.onc.leap.service.NlpSlsService;
import gov.hhs.onc.leap.sls.impl.ruledb.CSVBasedRestrictedCodesSLSRulesDB;
import gov.hhs.onc.leap.sls.service.data.entity.NlpSlsTask;
import gov.hhs.onc.leap.sls.service.repository.LabelingResultRepository;
import gov.hhs.onc.leap.sls.service.repository.NlpSlsTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.ws.rs.WebApplicationException;


/**
 * ddecouteau@saperi.io
 */
@Component
public class SLSStartupBean {
    private final static Logger LOGGER = Logger.getLogger(SLSStartupBean.class.getName());
    

    @Autowired 
    private ServiceConfigurations serviceConfigurations;
    
    @Autowired
    private LabelingResultRepository labelingResultRepository;

    @Autowired
    private NlpSlsTaskRepository nlpSLSTaskRepository;
    
    @Autowired 
    private LinkedBlockingQueue<String> nlpTaskQueue;
    
    @Autowired
    private CSVBasedRestrictedCodesSLSRulesDB slsRulesDB;
    
    private List<NlpSlsService> nlpSlsServiceThreads;
    
    @PostConstruct
    public void start() {


        try{

            nlpSlsServiceThreads = new ArrayList<>();
            for (int i=0; i < serviceConfigurations.getNumberOfNlpSlsThreads(); i++) {
                NlpSlsService nlpSlsServiceThread = new NlpSlsService(slsRulesDB, nlpTaskQueue, nlpSLSTaskRepository, labelingResultRepository);
                nlpSlsServiceThread.setName("NLP-SLS Service Thread-"+i);
                nlpSlsServiceThreads.add(nlpSlsServiceThread);
                nlpSlsServiceThread.start();
            }
            //load the queue with whatever remaining from previous (perhaps interrupted) run.
            {
                List<NlpSlsTask> nlpSlsTasks = nlpSLSTaskRepository.findAll();
                for (NlpSlsTask nlpSlsTask: nlpSlsTasks)
                    nlpTaskQueue.add(nlpSlsTask.getId());
            }



        }
        catch (Exception e)
        {
            LOGGER.log(Level.INFO,
                    String.format("Error occurred: %s %s", e.getClass().getCanonicalName(), e.getMessage()));
            //throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void stop() {


        for (NlpSlsService nlpSlsServiceThread : nlpSlsServiceThreads) {
            if (nlpSlsServiceThread != null && nlpSlsServiceThread.isAlive())
                nlpSlsServiceThread.interrupt();
        }

    }
}
