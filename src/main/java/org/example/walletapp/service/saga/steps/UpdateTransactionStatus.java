package org.example.walletapp.service.saga.steps;

import org.example.walletapp.entites.SagaStep;
import org.example.walletapp.service.saga.SagaContext;
import org.example.walletapp.service.saga.SagaStepInterface;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.walletapp.entites.Wallet;
import org.example.walletapp.entites.enums.SagaStatus;
import org.example.walletapp.repositories.WalletRepository;
import org.example.walletapp.service.saga.SagaContext;
import org.example.walletapp.service.saga.SagaStepInterface;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateTransactionStatus implements SagaStepInterface {
    @Override
    public boolean execute(SagaContext context) {
        Long transactionId = context.getLong("transactionId");
        log.info("Updating status of transaction {}", transactionId);

        return false;
    }

    @Override
    public boolean compensate(SagaContext context) {
        return false;
    }

    @Override
    public String getStepName() {
        return "";
    }
}
