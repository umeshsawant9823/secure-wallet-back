package com.app.wallet.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.wallet.entity.Wallet;
import com.app.wallet.entity.WalletTransaction;
import com.app.wallet.repository.WalletRepository;
import com.app.wallet.repository.WalletTransactionRepository;


@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository transactionRepository;

    // =====================================================
    // 🔹 STEP 8 + STEP 14.3: ADD MONEY (Idempotent + DB Safe)
    // =====================================================
    @Transactional
    public BigDecimal addMoney(
            Long userId,
            BigDecimal amount,
            String idempotencyKey) {

        // 1️⃣ Idempotency check
        var existingTx =
                transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existingTx.isPresent()) {
            return existingTx.get().getAmount();
        }

        // 2️⃣ Fetch wallet
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // 3️⃣ Add balance
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        // 4️⃣ Save transaction (DB unique constraint)
        WalletTransaction tx = new WalletTransaction();
        tx.setUserId(userId);
        tx.setAmount(amount);
        tx.setIdempotencyKey(idempotencyKey);

        try {
            transactionRepository.save(tx);
        } catch (DataIntegrityViolationException e) {
            // Parallel duplicate request
            return transactionRepository
                    .findByIdempotencyKey(idempotencyKey)
                    .get()
                    .getAmount();
        }

        return wallet.getBalance();
    }

    // =====================================================
    // 🔹 STEP 9 + STEP 14.4: TRANSFER MONEY
    // =====================================================
    @Transactional
    public void transferMoney(
            Long senderUserId,
            Long receiverUserId,
            BigDecimal amount,
            String idempotencyKey) {

        // 1️⃣ Idempotency check
        transactionRepository.findByIdempotencyKey(idempotencyKey)
                .ifPresent(tx -> {
                    throw new RuntimeException("Duplicate transfer request");
                });

        // 2️⃣ Fetch sender wallet
        Wallet senderWallet = walletRepository.findByUserId(senderUserId)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        // 3️⃣ Fetch receiver wallet
        Wallet receiverWallet = walletRepository.findByUserId(receiverUserId)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        // 4️⃣ Balance validation
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // 5️⃣ Debit sender
        senderWallet.setBalance(
                senderWallet.getBalance().subtract(amount)
        );

        // 6️⃣ Credit receiver
        receiverWallet.setBalance(
                receiverWallet.getBalance().add(amount)
        );

        // 7️⃣ Save wallets (Optimistic locking via @Version)
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // 8️⃣ Save transaction
        WalletTransaction tx = new WalletTransaction();
        tx.setUserId(senderUserId);
        tx.setAmount(amount.negate());
        tx.setIdempotencyKey(idempotencyKey);

        transactionRepository.save(tx);
        // 9️⃣ Commit / rollback handled by @Transactional
    }

    // =====================================================
    // 🔹 STEP 10: VIEW WALLET BALANCE
    // =====================================================
    public BigDecimal getBalance(Long userId) {

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return wallet.getBalance();
    }
}
