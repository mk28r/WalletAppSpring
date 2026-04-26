package org.example.walletapp.service.saga.steps;

import java.math.BigDecimal;

import org.example.walletapp.entites.Wallet;
import org.example.walletapp.repositories.WalletRepository;
import org.example.walletapp.service.saga.SagaContext;
import org.example.walletapp.service.saga.SagaStepInterface;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DebitSourceWalletStep implements SagaStepInterface {

    private final WalletRepository walletRepository;

    @Override
    public boolean execute(SagaContext context) {
        log.info("DebitSourceWalletStep executing");

        Long fromWalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Debiting source wallet {} with amount {}", fromWalletId, amount);

        Wallet wallet = walletRepository.findByIdWithLock(fromWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        walletRepository.updateBalanceByUserId(fromWalletId, wallet.getBalance().subtract(amount));

        log.info("Debit source wallet step executed successfully");
        return true;
    }

    @Override
    public boolean compensate(SagaContext context) {
        log.info("DebitSourceWalletStep compensating");

        Long fromWalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Reverting debit for source wallet {} with amount {}", fromWalletId, amount);

        Wallet wallet = walletRepository.findByIdWithLock(fromWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        walletRepository.updateBalanceByUserId(fromWalletId, wallet.getBalance().add(amount));

        log.info("Debit source wallet compensation executed successfully");
        return true;
    }

    @Override
    public String getStepName() {
        return "DEBIT_SOURCE_WALLET_STEP";
    }
}