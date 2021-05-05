package gov.hhs.onc.leap.sls.service.data.entity;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * ddecouteau@saperi.io
 */

@XmlRootElement()
@Entity
@Table(name = "LabelingResult")
public class LabelingResult implements Persistable<String> {

    @Id
    @Column(name = "id")
    private String  id;
    
    @Column(name = "msgType")
    private String msgType;
    
    @Column(name = "origin")
    private String origin;

    @Column(name = "status")
    private String status;

    @Column(name = "result")
    private String result;

    @Column(name = "processingTime")
    private String processingTime;

    @Lob
    @Column(name = "notes")
    private String notes;



    @Transient
    private boolean isNew = true;

    @XmlTransient
    public boolean getIsNew() {return isNew;}
    public void setIsNew(boolean isNew) { this.isNew = isNew;}

    @Override
    @XmlTransient
    public boolean isNew() { return isNew;}


    public LabelingResult() {}

    @XmlElement
    public String getId() { return id; }
    public void setId(String id) {this.id = id;}

    @XmlElement
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @XmlElement
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    @XmlElement
    public String getProcessingTime() {
        return processingTime;
    }
    public void setProcessingTime(String processingTime) {
        this.processingTime = processingTime;
    }

    @XmlElement
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return the msgType
     */
    @XmlElement
    public String getMsgType() {
        return msgType;
    }

    /**
     * @param msgType the msgType to set
     */
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    /**
     * @return the origin
     */
    @XmlElement
    public String getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
