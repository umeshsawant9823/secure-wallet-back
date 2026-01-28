package com.app.wallet.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransferRequest {
    private Long receiverUserId;
    private BigDecimal amount;
}
