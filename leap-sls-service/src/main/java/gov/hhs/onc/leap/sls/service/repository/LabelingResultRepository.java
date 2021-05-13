package gov.hhs.onc.leap.sls.service.repository;

import gov.hhs.onc.leap.sls.service.data.entity.LabelingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ddecouteau@saperi.io
 */
@Component
public interface LabelingResultRepository extends JpaRepository<LabelingResult, String> {

    List<LabelingResult> findAll();

    @Query("select a from LabelingResult a where a.id = :id")
    LabelingResult getLabelingResultById(@Param("id") String id);
    
    @Query("select a from LabelingResult a where a.msgType = :msgType")
    List<LabelingResult> getLabelingResultByMessageType(@Param("msgType") String msgType);
    
    @Query("select a from LabelingResult a where a.origin = :origin")
    List<LabelingResult> getLabelingResultByOrigin(@Param("origin") String origin);
    
    @Query("select a from LabelingResult a where a.id = :id and a.origin = :origin")
    LabelingResult getLabelingResultByIdAndOrigin(@Param("id") String id, @Param("origin") String origin);
    
    
    
    List<LabelingResult> findByStatus(String status);

}
