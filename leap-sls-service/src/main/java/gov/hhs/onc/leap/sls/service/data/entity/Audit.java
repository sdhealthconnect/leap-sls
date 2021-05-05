/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.onc.leap.sls.service.data.entity;

import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author duanedecouteau
 */
@Entity
@Table(name = "audit")
@XmlRootElement
public class Audit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idaudit")
    private Integer id;
    @Size(max = 80)
    @Column(name = "msg_uuid")
    private String msgUuid;
    @Column(name = "request_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;
    @Size(max = 45)
    @Column(name = "origin")
    private String origin;
    @Size(max = 45)
    @Column(name = "source")
    private String source;
    @Size(max = 10)
    @Column(name = "decision")
    private String decision;
    @Size(max = 45)
    @Column(name = "action")
    private String action;
    @Size(max = 45)
    @Column(name = "label")
    private String label;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "cds_request")
    private String cdsRequest;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "cds_response")
    private String cdsResponse;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "sls_response")
    private String slsResponse;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "sls_notes")
    private String slsNotes;

    public Audit() {
    }

    public Audit(Integer id) {
        this.id = id;
    }

    @XmlElement
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlElement
    public String getMsgUuid() {
        return msgUuid;
    }

    public void setMsgUuid(String msgUuid) {
        this.msgUuid = msgUuid;
    }

    @XmlElement
    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    @XmlElement
    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @XmlElement
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @XmlElement
    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    @XmlElement
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @XmlElement
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @XmlElement
    public String getCdsRequest() {
        return cdsRequest;
    }

    public void setCdsRequest(String cdsRequest) {
        this.cdsRequest = cdsRequest;
    }

    @XmlElement
    public String getCdsResponse() {
        return cdsResponse;
    }

    public void setCdsResponse(String cdsResponse) {
        this.cdsResponse = cdsResponse;
    }

    @XmlElement
    public String getSlsResponse() {
        return slsResponse;
    }

    public void setSlsResponse(String slsResponse) {
        this.slsResponse = slsResponse;
    }

    @XmlElement
    public String getSlsNotes() {
        return slsNotes;
    }

    public void setSlsNotes(String slsNotes) {
        this.slsNotes = slsNotes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Audit)) {
            return false;
        }
        Audit other = (Audit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.hhs.onc.leap.sls.service.data.entity.Audit[ idaudit=" + id + " ]";
    }
    
}
