package org.example.walletapp.service.saga.steps;

import org.example.walletapp.entites.Wallet;
import org.example.walletapp.service.saga.SagaContext;
import org.example.walletapp.service.saga.SagaStepInterface;

import java.math.BigDecimal;
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
public class DebitSourceWalletStep implements SagaStepInterface {
    WalletRepository walletRepository;
    @Override
    @Transactional
    public boolean execute(SagaContext context) {
        log.info("DebitSourceWalletStep executing");
        // step 1 : Get the destination wallet id from the context
        Long toWalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Debiting destination wallet {} with amount {}", toWalletId, amount);

        // step 2 : fetch the destination wallet from the database with a lock

        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // step 3 : debit the destination wallet
        walletRepository.updateBalanceByUserId(toWalletId, wallet.getBalance().subtract(amount));
        log.info("Wallet saved with balance {}", wallet.getBalance());
        context.put("toWalletBalanceAfterCredit", wallet.getBalance());

        //step 4 : update the context with the changes

        log.info("Debit destination wallet step executed successfully");
        return true;
    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context) {
        log.info("DebitSourceWalletStep compensating");
        // step 1 : Get the destination wallet id from the context
        Long toWalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Debiting destination wallet {} with amount {}", toWalletId, amount);

        // step 2 : fetch the destination wallet from the database with a lock

        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // step 3 : debit the destination wallet
        walletRepository.updateBalanceByUserId(toWalletId, wallet.getBalance().add(amount));
        log.info("Wallet saved with balance {}", wallet.getBalance());
        context.put("toWalletBalanceAfterCredit", wallet.getBalance());

        //step 4 : update the context with the changes

        log.info("Debit destination wallet step executed successfully");
        return true;
    }

    @Override
    @Transactional
    public String getStepName() {
        return "DEBIT_SOURCE_WALLET_STEP";
    }
}
