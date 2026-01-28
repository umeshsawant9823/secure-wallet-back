package com.app.wallet.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.wallet.entity.WalletTransaction;

public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, Long> {

    // 🔹 STEP 8 & 9 (Idempotency check)
    Optional<WalletTransaction> findByIdempotencyKey(String idempotencyKey);

    // 🔹 STEP 11 (Transaction History with Pagination)
    Page<WalletTransaction> findByUserId(Long userId, Pageable pageable);
}
	