package org.example.walletapp.Config;

import org.example.walletapp.service.saga.SagaStepInterface;
import org.example.walletapp.service.saga.steps.CreditDestinationWalletStep;
import org.example.walletapp.service.saga.steps.DebitSourceWalletStep;
import org.example.walletapp.service.saga.steps.SagaStepFactory;
import org.example.walletapp.service.saga.steps.UpdateTransactionStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class SagaConfiguration {

    @Bean
    public Map<String, SagaStepInterface> sagaStepMap(
            DebitSourceWalletStep debitSourceWalletStep,
            CreditDestinationWalletStep creditDestinationWalletStep,
            UpdateTransactionStatus updateTransactionStatus
    ) {
        Map<String, SagaStepInterface> map = new HashMap<>();

        map.put(debitSourceWalletStep.getStepName(), debitSourceWalletStep);
        map.put(creditDestinationWalletStep.getStepName(), creditDestinationWalletStep);
        map.put(updateTransactionStatus.getStepName(), updateTransactionStatus);

        return map;
    }
}