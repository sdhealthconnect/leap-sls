/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.onc.leap.sls.service.config;

import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by VHA Development Staff - Questions may be directed to Mike Davis, Security and Privacy Architect - 8/31/2016.
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
