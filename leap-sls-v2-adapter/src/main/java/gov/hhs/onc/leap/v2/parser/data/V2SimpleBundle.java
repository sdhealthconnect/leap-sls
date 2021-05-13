package gov.hhs.onc.leap.v2.parser.data;


import gov.hhs.onc.leap.sls.data.data.HCSBundle;
import gov.hhs.onc.leap.sls.data.data.HCSClinicalFact;
import gov.hhs.onc.leap.sls.data.data.HCSConceptDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * ddecouteau@saperi.io
 */

public class V2SimpleBundle implements HCSBundle {

    String id;  //msg control number
    private String sendingOrganization; // 
    private String messageType;
    private String requestDateTime;  //in form of yyyymmddhhmmss
    
    List<HCSConceptDescriptor> securityLabels = new ArrayList<HCSConceptDescriptor>();
    List<HCSClinicalFact> clinicalFacts = new ArrayList<HCSClinicalFact>();

    List<PlaintextClinicalFactAdapter> plainTextClinicalFacts = new ArrayList<PlaintextClinicalFactAdapter>();


    public V2SimpleBundle(String id, String sendingOrganization, String messageType, String requestDateTime)
    {
        this.id = id;
        this.messageType = messageType;
        this.requestDateTime = requestDateTime;
        this.sendingOrganization = sendingOrganization;
    }

    public String getId()
    {
        return id;
    }

    public void addPlainTextClinicalFact (PlaintextClinicalFactAdapter plaintextClinicalFactAdapter)
    {
        plainTextClinicalFacts.add(plaintextClinicalFactAdapter);
    }

    public List<PlaintextClinicalFactAdapter> getPlainTextClinicalFacts() {
        return plainTextClinicalFacts;
    }

    public void addClinicalFact (HCSClinicalFact clinicalFact)
    {
        if (! clinicalFacts.contains(clinicalFact))
            clinicalFacts.add(clinicalFact);
    }

    public void addClinicalFacts (List<HCSClinicalFact> clinicalFacts)
    {
        clinicalFacts.addAll(clinicalFacts);
    }

    public void addSecurityLabels (List<HCSConceptDescriptor> securityLabels) {
        for (HCSConceptDescriptor label: securityLabels)
            addSecurityLabel(label);
    }
    public void addSecurityLabel (HCSConceptDescriptor securityLabel) {
        if (!securityLabels.contains(securityLabel))
            securityLabels.add(securityLabel);
    }

    public boolean removeSecurityLabel (HCSConceptDescriptor hcsConceptDescriptor) {
        //don't care for now
        return true;
    }

    public List<HCSConceptDescriptor> getSecurityLabels() {
        return securityLabels;
    }

    public List<HCSClinicalFact> getClinicalFacts() {
        return clinicalFacts;
    }

    public void deleteClinicalFact (String s) {
        //don't care for now
    }

    /**
     * @return the sendingOrganization
     */
    public String getSendingOrganization() {
        return sendingOrganization;
    }

    /**
     * @param sendingOrganization the sendingOrganization to set
     */
    public void setSendingOrganization(String sendingOrganization) {
        this.sendingOrganization = sendingOrganization;
    }

    /**
     * @return the messageType
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * @param messageType the messageType to set
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    /**
     * @return the requestDateTime
     */
    public String getRequestDateTime() {
        return requestDateTime;
    }

    /**
     * @param requestDateTime the requestDateTime to set
     */
    public void setRequestDateTime(String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }
}
