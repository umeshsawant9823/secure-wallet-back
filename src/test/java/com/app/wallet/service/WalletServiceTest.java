package com.app.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.wallet.entity.Wallet;
import com.app.wallet.entity.WalletTransaction;
import com.app.wallet.exception.InsufficientBalanceException;
import com.app.wallet.exception.ResourceNotFoundException;
import com.app.wallet.repository.WalletRepository;
import com.app.wallet.repository.WalletTransactionRepository;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

	@Mock
	private WalletRepository walletRepository;

	@Mock
	private WalletTransactionRepository transactionRepository;

	@InjectMocks
	private WalletService walletService;

	// -------------------------------
	// ADD MONEY - SUCCESS
	// -------------------------------
	@Test
	void addMoney_success() {

		Wallet wallet = new Wallet();
		wallet.setBalance(BigDecimal.valueOf(100));

		when(transactionRepository.findByIdempotencyKey("key-1")).thenReturn(Optional.empty());

		when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

		BigDecimal result = walletService.addMoney(1L, BigDecimal.valueOf(50), "key-1");

		assertEquals(BigDecimal.valueOf(150), result);
		verify(transactionRepository, times(1)).save(any());
	}

	// -------------------------------
	// ADD MONEY - IDEMPOTENT
	// -------------------------------
	@Test
	void addMoney_duplicateRequest_returnsSameAmount() {

		WalletTransaction tx = new WalletTransaction();
		tx.setAmount(BigDecimal.valueOf(50));

		when(transactionRepository.findByIdempotencyKey("dup-key")).thenReturn(Optional.of(tx));

		BigDecimal result = walletService.addMoney(1L, BigDecimal.valueOf(50), "dup-key");

		assertEquals(BigDecimal.valueOf(50), result);
		verify(walletRepository, never()).save(any());
	}

	// -------------------------------
	// TRANSFER - INSUFFICIENT BALANCE
	// -------------------------------
	@Test
	void transferMoney_insufficientBalance() {

		Wallet sender = new Wallet();
		sender.setBalance(BigDecimal.valueOf(10));

		Wallet receiver = new Wallet();
		receiver.setBalance(BigDecimal.ZERO);

		when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(sender));
		when(walletRepository.findByUserId(2L)).thenReturn(Optional.of(receiver));

		assertThrows(InsufficientBalanceException.class,
				() -> walletService.transferMoney(1L, 2L, BigDecimal.valueOf(100), "tx-1"));
	}

	// -------------------------------
	// WALLET NOT FOUND
	// -------------------------------
	@Test
	void getBalance_walletNotFound() {

		when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> walletService.getBalance(1L));
	}
}
