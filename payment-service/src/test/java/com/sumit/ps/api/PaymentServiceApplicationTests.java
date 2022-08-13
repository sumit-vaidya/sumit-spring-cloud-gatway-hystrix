package com.sumit.ps.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sumit.ps.api.entity.Payment;
import com.sumit.ps.api.repository.PaymentRepository;
import com.sumit.ps.api.service.PaymentService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PaymentServiceApplicationTests {

	@MockBean
	private PaymentRepository repository;
	
	@Autowired
	private PaymentService service;
		
	@Test
	public void contextLoads() { }
	
	@Test
	public void findPaymentHistoryByOrderIdTest() throws JsonProcessingException {
		Payment payment = getPayment();
		when(repository.findByOrderId(Mockito.anyInt())).thenReturn(payment);
		assertNotNull(service.findPaymentHistoryByOrderId(payment.getOrderId()));		
	}
	
	@Test
	public void doPaymentTest() throws JsonProcessingException {
		Payment payment = getPayment();
		when(repository.save(Mockito.any(Payment.class))).thenReturn(payment);
		assertEquals(payment.getOrderId(), service.doPayment(payment).getOrderId());
	}

	private Payment getPayment() {
		Payment payment = new Payment();
		payment.setAmount(101.00);
		payment.setOrderId(101);
		payment.setPaymentId(100);
		payment.setPaymentStatus(service.paymentProcessing());
		payment.setTransactionId(UUID.randomUUID().toString());
		return payment;
	}
}
