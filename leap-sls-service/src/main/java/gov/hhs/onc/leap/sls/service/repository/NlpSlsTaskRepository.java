package gov.hhs.onc.leap.sls.service.repository;

import gov.hhs.onc.leap.sls.service.data.entity.NlpSlsTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

/**
 * ddecouteau@saperi.io
 */
public interface NlpSlsTaskRepository extends JpaRepository<NlpSlsTask, String> {

    List<NlpSlsTask> findAll();

    @Query("select a from NlpSlsTask a where a.id = :id")
    NlpSlsTask getNlpSlsTaskById(@Param("id") String id);
    
    

}
