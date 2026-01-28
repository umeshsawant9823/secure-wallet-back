package com.app.wallet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.wallet.entity.Wallet;
import com.app.wallet.entity.WalletTransaction;
import com.app.wallet.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin APIs", description = "Admin-only wallet and transaction operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "View all wallets", description = "Admin can view all wallets in the system")
    @GetMapping("/wallets")
    public List<Wallet> viewAllWallets() {
        return adminService.getAllWallets();
    }

    @Operation(summary = "View all transactions", description = "Admin can view all wallet transactions")
    @GetMapping("/transactions")
    public List<WalletTransaction> viewAllTransactions() {
        return adminService.getAllTransactions();
    }
}
