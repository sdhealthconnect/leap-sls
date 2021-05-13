package gov.hhs.onc.leap.sls.ccda.parser;

import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ddecouteau@saperi.io
 */
public class CCDANamespaceProvider implements NamespaceContext {
    private final Map<String, String> PREF_MAP = new HashMap<String, String>();


    public CCDANamespaceProvider() {
        PREF_MAP.put("ccda", "urn:hl7-org:v3");

    }

    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("No prefix provided!");
//        } else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
//            return defaultNS;
        }
        else {
            return PREF_MAP.get(prefix);
        }
    }

    public String getPrefix(String uri) {
        throw new UnsupportedOperationException();
    }

    public Iterator getPrefixes(String uri) {
        throw new UnsupportedOperationException();
    }

}
