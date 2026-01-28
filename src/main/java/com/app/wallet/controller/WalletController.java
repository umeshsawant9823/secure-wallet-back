package com.app.wallet.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.wallet.dto.TransferRequest;
import com.app.wallet.service.WalletService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/wallet")
@Tag(name = "Wallet APIs", description = "Wallet balance, add money and transfer operations")
@SecurityRequirement(name = "bearerAuth")
public class WalletController {

    @Autowired
    private WalletService walletService;

    // 🔹 Add Money
    @Operation(summary = "Add money to wallet", description = "Add money using idempotency key")
    @PostMapping("/add")
    public BigDecimal addMoney(
            @RequestHeader("Idempotency-Key")
            @Parameter(description = "Unique idempotency key") String idempotencyKey,

            @RequestParam BigDecimal amount,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return walletService.addMoney(userId, amount, idempotencyKey);
    }

    // 🔹 Transfer Money
    @Operation(summary = "Transfer money", description = "Transfer money to another wallet (idempotent & safe)")
    @PostMapping("/transfer")
    public String transferMoney(
            @RequestHeader("Idempotency-Key")
            @Parameter(description = "Unique idempotency key") String idempotencyKey,

            @RequestBody TransferRequest request,
            Authentication authentication) {

        Long senderUserId = (Long) authentication.getPrincipal();

        walletService.transferMoney(
                senderUserId,
                request.getReceiverUserId(),
                request.getAmount(),
                idempotencyKey
        );

        return "Transfer successful";
    }

    // 🔹 View Balance
    @Operation(summary = "View wallet balance", description = "Get current wallet balance")
    @GetMapping("/view")
    public BigDecimal viewWallet(Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return walletService.getBalance(userId);
    }
}
