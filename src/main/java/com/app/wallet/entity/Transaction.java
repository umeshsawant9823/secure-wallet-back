package com.app.wallet.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions", uniqueConstraints = @UniqueConstraint(columnNames = "idempotencyKey"))
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long fromWallet; // Sender
	private Long toWallet; // Receiver

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(nullable = false)
	private String type; // ADD or TRANSFER

	@Column(nullable = false)
	private String status; // SUCCESS or FAILED

	@Column(nullable = false, unique = true)
	private String idempotencyKey;

	private LocalDateTime createdAt;

}
