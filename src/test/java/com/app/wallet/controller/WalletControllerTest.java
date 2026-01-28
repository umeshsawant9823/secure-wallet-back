package com.app.wallet.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

	@Autowired
	private MockMvc mockMvc;

	// -------------------------------
	// UNAUTHORIZED (401)
	// -------------------------------
	@Test
	void getBalance_unauthorized() throws Exception {
		mockMvc.perform(get("/api/wallet/balance")).andExpect(status().isUnauthorized());
	}

	// -------------------------------
	// AUTHORIZED USER (200)
	// -------------------------------
	@Test
	@WithMockUser(roles = "USER")
	void getBalance_authorized() throws Exception {
		mockMvc.perform(get("/api/wallet/balance")).andExpect(status().isOk());
	}

	// -------------------------------
	// FORBIDDEN (403)
	// -------------------------------
	@Test
	@WithMockUser(roles = "ADMIN")
	void userEndpoint_forbiddenForAdmin() throws Exception {
		mockMvc.perform(get("/api/wallet/balance")).andExpect(status().isForbidden());
	}

	// -------------------------------
	// IDEMPOTENT API TEST
	// -------------------------------
	@Test
	@WithMockUser(roles = "USER")
	void addMoney_idempotent() throws Exception {

		String body = """
				{
				  "amount": 100,
				  "idempotencyKey": "idem-123"
				}
				""";

		mockMvc.perform(post("/api/wallet/add").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isOk());

		mockMvc.perform(post("/api/wallet/add").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isOk());
	}
}
