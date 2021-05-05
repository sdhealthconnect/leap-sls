package gov.hhs.onc.leap.sls.data.pps;

import gov.hhs.onc.leap.sls.data.data.HCSBundle;

import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public interface PPSActor {
    HCSBundle apply (HCSBundle bundle, List<String> notes);
}
