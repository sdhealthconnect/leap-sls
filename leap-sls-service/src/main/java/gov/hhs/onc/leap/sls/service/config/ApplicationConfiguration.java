
package gov.hhs.onc.leap.sls.service.config;

import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ddecouteau@saperi.io
 */
@Configuration
public class ApplicationConfiguration {
    /**
     * There is a difference loading classpath resources between a self-executable jar and the tomcat 7 plugin
     * @return 
     */
    @Bean(name = "sens-codes-csv")
    public URL codesdb(){
        URL retval = this.getClass().getClassLoader().getResource("sens-codes.csv");
        if(retval == null)
            retval= this.getClass().getClassLoader().getResource("/WEB-INF/classes/sens-codes.csv");
        
        return retval;
    }
    @Bean()
    @Qualifier("taskQueue")
    public LinkedBlockingQueue createBlockingQueue(){
        return new LinkedBlockingQueue();
    }
}
