package com.example.busticketbooking.payment.controller;

import com.example.busticketbooking.payment.dto.PaymentRequest;
import com.example.busticketbooking.payment.dto.PaymentResponse;
import com.example.busticketbooking.payment.service.PaymentService;
import com.example.busticketbooking.shared.exception.ExpiredCouponCodeException;
import com.example.busticketbooking.shared.exception.GlobalExceptionHandler;
import com.example.busticketbooking.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
class PaymentControllerIntegrationTest {
    private static final String BASE_URL = "/api/v1/pay";

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void payReservation_withValidCoupon_shouldReturnOk() throws Exception {
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(new PaymentResponse("Coupon payment successful"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reservationId\": 1, \"method\": \"COUPON\", \"transactionType\": \"TICKET_PURCHASE\", \"couponCode\": \"TEST100\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Coupon payment successful"));
    }

    @Test
    void payReservation_withNonExistentCoupon_shouldReturnNotFound() throws Exception {
        when(paymentService.processPayment(any(PaymentRequest.class))).thenThrow(new NotFoundException("Coupon not found"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reservationId\": 1, \"method\": \"COUPON\", \"transactionType\": \"TICKET_PURCHASE\", \"couponCode\": \"INVALID\"}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Coupon not found"));
    }

    @Test
    void payReservation_withNotValidCoupon_shouldReturnBadRequest() throws Exception {
        when(paymentService.processPayment(any(PaymentRequest.class))).thenThrow(new ExpiredCouponCodeException("EXPIRED"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reservationId\": 1, \"method\": \"COUPON\", \"transactionType\": \"TICKET_PURCHASE\", \"couponCode\": \"EXPIRED\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Coupon code 'EXPIRED' has expired."));
    }

    @Test
    void payReservation_withWallet_shouldReturnOk() throws Exception {
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(new PaymentResponse("Wallet payment successful"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reservationId\": 1, \"method\": \"WALLET\", \"transactionType\": \"TICKET_PURCHASE\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Wallet payment successful"));
    }
}
