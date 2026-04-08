package org.example.walletapp.repositories;


import java.util.List;
import java.util.Optional;

import org.example.walletapp.entites.SagaStep;
import org.example.walletapp.entites.enums.StepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {

    List<SagaStep> findBySagaInstanceId(Long sagaInstanceId);

    List<SagaStep> findBySagaInstanceIdAndStatus(Long sagaInstanceId, StepStatus status);

    Optional<SagaStep> findBySagaInstanceIdAndStepNameAndStatus(Long sagaInstanceId, String stepName, StepStatus status);

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstanceId = :sagaInstanceId AND s.status = 'COMPLETED' ")
    List<SagaStep> findCompletedStepsBySagaInstanceId( @Param("sagaInstanceId") Long sagaInstanceId );
}
