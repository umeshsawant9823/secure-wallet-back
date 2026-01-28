package com.app.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.app.wallet.entity.User;
import com.app.wallet.entity.Wallet;
import com.app.wallet.repository.WalletRepository;

@SpringBootTest
class WalletConcurrencyTest {

	@Autowired
	private WalletService walletService;

	@Autowired
	private WalletRepository walletRepository;

	@Test
	void concurrentTransfers_shouldRemainConsistent() throws Exception {

		// ✅ Create users
		User senderUser = new User();
		senderUser.setId(1L);

		User receiverUser = new User();
		receiverUser.setId(2L);

		// ✅ Create wallets
		Wallet sender = new Wallet();
		sender.setUser(senderUser);
		sender.setBalance(BigDecimal.valueOf(1000));

		Wallet receiver = new Wallet();
		receiver.setUser(receiverUser);
		receiver.setBalance(BigDecimal.ZERO);

		walletRepository.save(sender);
		walletRepository.save(receiver);

		Runnable transferTask = () -> {
			try {
				walletService.transferMoney(1L, 2L, BigDecimal.valueOf(100), UUID.randomUUID().toString());
			} catch (Exception ignored) {
			}
		};

		Thread t1 = new Thread(transferTask);
		Thread t2 = new Thread(transferTask);

		t1.start();
		t2.start();

		t1.join();
		t2.join();

		Wallet updatedSender = walletRepository.findByUserId(1L).get();
		Wallet updatedReceiver = walletRepository.findByUserId(2L).get();

		assertEquals(BigDecimal.valueOf(800), updatedSender.getBalance());
		assertEquals(BigDecimal.valueOf(200), updatedReceiver.getBalance());
	}
}
