package com.example.busticketbooking.payment.controller;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.service.WalletRechargeService;
import com.example.busticketbooking.shared.exception.ExpiredCouponCodeException;
import com.example.busticketbooking.shared.exception.GlobalExceptionHandler;
import com.example.busticketbooking.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class WalletControllerIntegrationTest {

    private static final String BASE_URL = "/api/v1/wallet";

    @MockitoBean
    private WalletRechargeService walletRechargeService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void rechargeWallet_withValidCoupon_shouldReturnOk() throws Exception {
        when(walletRechargeService.recharge(any(PaymentRequest.class)))
                .thenReturn(new PaymentResponse("Wallet recharge successful"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/recharge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"method\": \"COUPON\", \"transactionType\": \"WALLET_RECHARGE\", \"couponCode\": \"TEST100\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Wallet recharge successful"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void rechargeWallet_withNonExistentCoupon_shouldReturnNotFound() throws Exception {
        when(walletRechargeService.recharge(any(PaymentRequest.class)))
                .thenThrow(new NotFoundException("Coupon not found"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/recharge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"method\": \"COUPON\", \"transactionType\": \"WALLET_RECHARGE\", \"couponCode\": \"INVALID\"}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Coupon not found"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void rechargeWallet_withNotValidCoupon_shouldReturnBadRequest() throws Exception {
        when(walletRechargeService.recharge(any(PaymentRequest.class)))
                .thenThrow(new ExpiredCouponCodeException("EXPIRED"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/recharge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"method\": \"COUPON\", \"transactionType\": \"WALLET_RECHARGE\", \"couponCode\": \"EXPIRED\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Coupon code 'EXPIRED' has expired."));
    }

    @Test
    void rechargeWallet_withoutAuthentication_shouldReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/recharge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"method\": \"COUPON\", \"transactionType\": \"WALLET_RECHARGE\", \"couponCode\": \"TEST100\"}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
