package gov.hhs.onc.leap.sls.service.util;

import gov.hhs.onc.leap.sls.service.data.LabelingTask;
import gov.hhs.onc.leap.sls.service.data.entity.LabelingResult;
import com.google.common.base.Joiner;
import gov.hhs.onc.leap.sls.ccda.parser.data.CCDASimpleBundle;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;
import gov.hhs.onc.leap.v2.parser.data.V2SimpleBundle;
import java.util.ArrayList;
import java.util.List;


/**
 * ddecouteau@saperi.io
 */
public class LabelingTaskToLabelingResult {

    public static String TASK_STATUS_COMPLETED = "COMPLETED";
    public static String TASK_STATUS_INPROGRESS = "IN-PROGRESS";

    public static String RECORD_STATUS_RESTRICTED = "RESTRICTED";
    public static String RECORD_STATUS_NON_RESTRICTED = "NON-RESTRICTED";



    public static LabelingResult convertCompletedTask (LabelingTask task)
    {
        LabelingResult result = new LabelingResult();
        String id = "";
        String msgType = "";
        String origin = "";
        List<HCSConceptDescriptor> securityLabels = new ArrayList<HCSConceptDescriptor>();
        if (task.getRecord() instanceof CCDASimpleBundle) {
            CCDASimpleBundle ccda = (CCDASimpleBundle)task.getRecord();
            id = ccda.getId();
            securityLabels = ccda.getSecurityLabels();
            msgType = "CCDA";
        }
        else if (task.getRecord() instanceof V2SimpleBundle) {
            V2SimpleBundle v2 = (V2SimpleBundle)task.getRecord();
            id = v2.getId();
            securityLabels = v2.getSecurityLabels();
            msgType = "v2";
            origin = v2.getSendingOrganization();
        }
        result.setId(id);
        result.setMsgType(msgType);
        result.setOrigin(origin);
        result.setNotes(Joiner.on("\n").join(task.getComments()));
        result.setProcessingTime(task.getProcessingTimeInMillis()+"");
        result.setStatus(TASK_STATUS_COMPLETED);

        if (securityLabels.size() !=0) {
            result.setResult(RECORD_STATUS_RESTRICTED);
        }
        else {
            result.setResult(RECORD_STATUS_NON_RESTRICTED);
        }
        return result;
    }

    public static LabelingResult convertIncompletedTask (LabelingTask task)
    {
        LabelingResult result = new LabelingResult();
        String id = "";
        if (task.getRecord() instanceof CCDASimpleBundle) {
            CCDASimpleBundle ccda = (CCDASimpleBundle)task.getRecord();
            id = ccda.getId();
        }
        else if (task.getRecord() instanceof V2SimpleBundle) {
            V2SimpleBundle v2 = (V2SimpleBundle)task.getRecord();
            id = v2.getId();
        }

        result.setId(id);
        result.setNotes(Joiner.on("\n").join(task.getComments()));
        result.setProcessingTime(task.getProcessingTimeInMillis()+"");
        result.setStatus(TASK_STATUS_INPROGRESS);

        return result;
    }
}
