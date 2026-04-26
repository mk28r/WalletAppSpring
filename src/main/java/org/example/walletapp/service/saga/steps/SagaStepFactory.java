package org.example.walletapp.service.saga.steps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.walletapp.service.saga.SagaStepInterface;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class SagaStepFactory {

    private final Map<String, SagaStepInterface> sagaStepMap = new HashMap<>();
    private final List<SagaStepInterface> sagaSteps;

    public SagaStepFactory(List<SagaStepInterface> sagaSteps) {
        this.sagaSteps = sagaSteps;
    }

    @PostConstruct
    public void init() {
        for (SagaStepInterface step : sagaSteps) {
            sagaStepMap.put(step.getStepName(), step);
            System.out.println(step.getStepName() + " -> " + step.getClass().getSimpleName());
        }
    }

    public enum SagaStepType {
        DEBIT_SOURCE_WALLET_STEP,
        CREDIT_DESTINATION_WALLET_STEP,
        UPDATE_TRANSACTION_STATUS_STEP
    }

    public static final List<SagaStepType> TransferMoneySagaSteps = List.of(
            SagaStepType.DEBIT_SOURCE_WALLET_STEP,
            SagaStepType.CREDIT_DESTINATION_WALLET_STEP,
            SagaStepType.UPDATE_TRANSACTION_STATUS_STEP
    );

    public SagaStepInterface getSagaStep(String stepName) {
        SagaStepInterface step = sagaStepMap.get(stepName);
        if (step == null) {
            throw new IllegalArgumentException("No saga step found for: " + stepName);
        }
        return step;
    }
}