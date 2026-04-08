package org.example.walletapp.service.saga.steps;

import org.example.walletapp.entites.SagaStep;
import org.example.walletapp.entites.Transaction;
import org.example.walletapp.entites.TransactionStatus;
import org.example.walletapp.repositories.TransactionRepository;
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
    private final TransactionRepository transactionRepository;

    @Override
    public boolean execute(SagaContext context) {
        Long transactionId = context.getLong("transactionId");
        log.info("Updating status of transaction {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId) .orElseThrow(() -> new RuntimeException("Transaction not found"));
        context.put("originalTransactionStatus", transaction.getStatus());

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        log.info("Transaction status updated for transaction {}", transactionId);

        context.put("transactionStatusAfterUpdate", transaction.getStatus());

        log.info("Update transaction status step executed successfully");


        return true;
    }

    @Override
    public boolean compensate(SagaContext context) {
        Long transactionId = context.getLong("transactionId");

        TransactionStatus originalTransactionStatus = TransactionStatus.valueOf(context.getString("originalTransactionStatus"));

        log.info("Compensating transaction status for transaction {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setStatus(originalTransactionStatus);
        transactionRepository.save(transaction);

        log.info("Transaction status compensated for transaction {}", transactionId);

        return true;
    }

    @Override
    public String getStepName() {
        return "UPDATE_TRANSACTION_STATUS_STEP";
    }
}
