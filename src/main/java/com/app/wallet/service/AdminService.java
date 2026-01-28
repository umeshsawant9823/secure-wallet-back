package com.app.wallet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.wallet.entity.Wallet;
import com.app.wallet.entity.WalletTransaction;
import com.app.wallet.repository.WalletRepository;
import com.app.wallet.repository.WalletTransactionRepository;

@Service
public class AdminService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository transactionRepository;

    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    public List<WalletTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
