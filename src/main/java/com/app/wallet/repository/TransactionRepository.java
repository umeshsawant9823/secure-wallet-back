package com.app.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.wallet.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	// Check if transaction exists by Idempotency Key
	Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

	// Fetch transactions by wallet (sender or receiver)
	List<Transaction> findByFromWalletOrToWallet(Long fromWallet, Long toWallet);
}
