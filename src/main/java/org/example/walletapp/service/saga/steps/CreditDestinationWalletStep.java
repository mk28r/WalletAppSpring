package org.example.walletapp.service.saga.steps;

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
@Transactional
public class CreditDestinationWalletStep implements SagaStepInterface {
    private final WalletRepository walletRepository ;

    @Override
    public boolean execute(SagaContext context) {
        log.info("CreditDestinationWalletStep executing");
        // step 1 : Get the destination wallet id from the context
        Long toWalletId = context.getLong("toWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Crediting destination wallet {} with amount {}", toWalletId, amount);

        // step 2 : fetch the destination wallet from the database with a lock

        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // step 3 : credit the destination wallet
        walletRepository.updateBalanceByUserId(toWalletId, wallet.getBalance().add(amount));
        log.info("Wallet saved with balance {}", wallet.getBalance());
        context.put("toWalletBalanceAfterCredit", wallet.getBalance());

        //step 4 : update the context with the changes

        log.info("Credit destination wallet step executed successfully");
        return true;
    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context) {
        // Step 1: Get the destination wallet id from the context
        Long toWalletId = context.getLong("toWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Compensation credit of destination wallet {} with amount {}", toWalletId, amount);

        // Step 2: Fetch the destination wallet from the database with a lock
        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        log.info("Wallet fetched with balance {}", wallet.getBalance());

        // Step 3: Credit the destination wallet

        walletRepository.updateBalanceByUserId(toWalletId, wallet.getBalance().subtract(amount));
        log.info("Wallet saved with balance {}", wallet.getBalance());
        context.put("toWalletBalanceAfterCreditCompensation", wallet.getBalance());
        // TODO: Once the context is updated in memory, we need to update the context in the database

        log.info("Credit compensation of destination wallet step executed successfully");
        return true;
    }

    @Override
    public String getStepName() {
        return "CreditDestinationWalletStep";
    }
}
