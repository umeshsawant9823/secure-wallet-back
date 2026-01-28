package com.app.wallet.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AddMoneyRequest {
    private BigDecimal amount;
}
