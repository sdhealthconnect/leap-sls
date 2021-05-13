package gov.hhs.onc.leap.sls.service.data;


import gov.hhs.onc.leap.fhir.parser.data.FHIRR4BundleAdapter;
import gov.hhs.onc.leap.sls.ccda.parser.data.CCDASimpleBundle;
import gov.hhs.onc.leap.v2.parser.data.V2SimpleBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public class LabelingTask {
    Object record;
    
    List<String> comments = new ArrayList<String>();
    long processingTimeInMillis = 0;

    public LabelingTask(CCDASimpleBundle record)
    {
        this.record = record;
    }
    
    public LabelingTask(V2SimpleBundle record) 
    {
        this.record = record;
    }

    public LabelingTask(FHIRR4BundleAdapter record) { this.record = record; }

    public void addComment(String comment)
    {
        comments.add(comment);
    }

    public void addComments(List<String> comments)
    {
        this.comments.addAll(comments);
    }

    public void purgeComments()
    {
        comments.clear();
    }

    public void addToProcessingTime (long milliSeconds)
    {
        processingTimeInMillis += milliSeconds;
    }

    public void resetProcessingTime()
    {
        processingTimeInMillis = 0;
    }

    public Object getRecord() {
        return record;
    }

    public List<String> getComments() {
        return comments;
    }

    public long getProcessingTimeInMillis() {
        return processingTimeInMillis;
    }
}
