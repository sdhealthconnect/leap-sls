package gov.hhs.onc.leap.sls.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ddecouteau@saperi.io
 */
@Component
public class ServiceConfigurations {

    @Value("5")
    int numberOfNlpSlsThreads;

    public int getNumberOfNlpSlsThreads() {
        return numberOfNlpSlsThreads;
    }

    public void setNumberOfNlpSlsThreads(int numberOfNlpSlsThreads) {
        this.numberOfNlpSlsThreads = numberOfNlpSlsThreads;
    }
}
