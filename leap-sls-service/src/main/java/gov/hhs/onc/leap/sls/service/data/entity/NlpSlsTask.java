package gov.hhs.onc.leap.sls.service.data.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ddecouteau@saperi.io
 */
@XmlRootElement()
@Entity
@Table(name = "NlpSlsTask")
public class NlpSlsTask {

//    public static String WAITING_STATE="WAITING";
//    public static String PROCESSING_STATE="PROCESSING";
//    public static String ERROR_STATE="ERROR";


    @Id
    @Column(name = "id")
    private String  id;

    @Lob
    @Column(name = "content")
    private String content;

//    @Column (name = "state")
//    private String state;

    @XmlElement
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

//    public String getState() {return state;}
//    public void setState(String state) {this.state = state;}
}
