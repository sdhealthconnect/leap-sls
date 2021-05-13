package gov.hhs.onc.leap.sls.service.repository;

import gov.hhs.onc.leap.sls.service.data.entity.NlpSlsTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * ddecouteau@saperi.io
 */
public interface NlpSlsTaskRepository extends JpaRepository<NlpSlsTask, String> {

    List<NlpSlsTask> findAll();

    @Query("select a from NlpSlsTask a where a.id = :id")
    NlpSlsTask getNlpSlsTaskById(@Param("id") String id);
    
    

}
