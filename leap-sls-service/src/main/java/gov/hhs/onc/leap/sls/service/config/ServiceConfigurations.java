package gov.hhs.onc.leap.sls.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by VHA Development Staff - Questions may be directed to Mike Davis, Security and Privacy Architect - 8/31/2016.
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
