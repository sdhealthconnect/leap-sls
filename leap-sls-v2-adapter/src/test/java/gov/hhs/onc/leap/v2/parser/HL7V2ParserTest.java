/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.onc.leap.v2.parser;

import gov.hhs.onc.leap.v2.parser.data.V2SimpleBundle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;



/**
 *
 * @author duanedecouteau
 */
public class HL7V2ParserTest {
    private HL7V2Parser processor;
    private String message;
    private String messageV23;
    private String messageHeader;
    private String messageDiagnosis;
    
    public HL7V2ParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        processor = new HL7V2Parser();
        message = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v23|T|2.3\n"
  			+ "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\n"
  			+ "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\n"
 			+ "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\n"
  			+ "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\n"
  			+ "OBX|1|ST|||Test Value";
        
        messageV23 = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v23|T|2.3\r"
  			+ "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\n"
  			+ "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\n"
 			+ "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\n"
  			+ "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\n"
  			+ "OBX|1|ST|||Test Value";
        messageHeader ="MSH|^~\\&|SENDING_APPLICATION|SENDING_FACILITY|RECEIVING_APPLICATION|RECEIVING_FACILITY|20110614075841||ACK|1407511|P|2.4||||||\r\n" + 
                    "MSA|AA|1407511|Success||";
        
        messageDiagnosis = "MSH||SendingApp^‹OID›^ISO|SendingFac^‹OID›^ISO|ReceivingApp^‹OID›^ISO|ReceivingFac^‹OID›^ISO|2007509101832133||ADT^A08^ADT_A01|20075091019450028|D|2.5\n"
                           + "EVN||2007509101832133\n"
                           + "PID|1||P410005^^^&2.16.840.1.114222.4.3.2.1&ISO||””||198805|F||2106-3^White^2.16.840.1.113883.6.238^W^White^L|^^^OR^97006\n"
                           + "PV1|1|E||||||||||||||||||||||||||||||||||||||||||200750910182522\n"
                           + "PV2|||^^^^SOB, looks dusky and is coughing up blood. States has just gotten over the measles.\n"
                           + "OBX|1|TS|11368-8^ILLNESS/INJURY ONSET DATE/TIME^LN||2007509092230||||||F\n"
                           + "OBX|2|NM|8310-5^BODY TEMPERATURE^LN||101.3|[degF]^^UCUM|||||F|||200750816124045\n"
                           + "OBX|3|SN|35094-2^BLOOD PRESSURE PANEL^LN||^140^/^84|mm[Hg]^Millimeters of Mercury^UCUM|||||F\n"
                           + "DG1|1||055.1^POSTMEASLES PNEUMONIA^I9C||200750816|W\n"
                           + "DG1|2||786.09^DYSPNEA/RESP.ABNORMALITIES^I9C||200750816|W\n"
                           + "DG1|3||786.3^HEMOPTYSIS^I9C||200750816|W\n"
                           + "DG1|4||782.5^CYANOSIS^I9C||200750816|W\n"
                           + "DG1|5||42^Human immunodeficiency virus [HIV] disease^I9C||200750816|W";
    }
    
    @After
    public void tearDown() {
    }

//    @Test
//    public void parsingTestHEADER() {
//        V2SimpleBundle bundle = processor.parseMessage(messageHeader);
//    
//        assert(bundle.getRequestDateTime().equals("20110614075841"));
//        assert(bundle.getSendingOrganization().equals("SENDING_FACILITY"));
//        assert(bundle.getId().equals("1407511"));
//    }
//    
//    @Test
//    public void parsingTestV25() {
//        V2SimpleBundle bundle = processor.parseMessage(message);
//
//        assert(bundle.getSendingOrganization().equals("TML"));
//        assert(bundle.getClinicalFacts().size() > 1);
//        assert(bundle.getClinicalFacts().get(0).getCodes().get(0).getCode().equals("T09-100442-RET-0"));
//    }
//    
//    @Test
//    public void parsingTestV23() {
//        V2SimpleBundle bundle = processor.parseMessage(messageV23);
//
//        assert(bundle.getSendingOrganization().equals("TML"));
//        assert(bundle.getClinicalFacts().size() > 1);
//        assert(bundle.getClinicalFacts().get(0).getCodes().get(0).getCode().equals("T09-100442-RET-0"));
//        
//    }
    
    @Test
    public void parsingTestDG1() {
        V2SimpleBundle bundle = processor.parseMessage(messageDiagnosis);
        
        assert(bundle.getClinicalFacts().size() > 1);
    }
}
