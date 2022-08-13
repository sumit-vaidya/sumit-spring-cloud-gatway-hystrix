package com.sumit.os.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sumit.os.api.common.Payment;
import com.sumit.os.api.common.TransactionRequest;
import com.sumit.os.api.common.TransactionResponse;
import com.sumit.os.api.entity.Order;
import com.sumit.os.api.repository.OrderRepository;
import com.sumit.os.api.service.OrderService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OrderServiceApplicationTests {

	@MockBean
	private OrderRepository repository;
	
	@MockBean 
	private RestTemplate template;
	
	@Autowired
	private OrderService service;
	
	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(service, "ENDPOINT_URL", "http://PAYMENT-SERVICE/payment/doPayment");
		//ReflectionTestUtils.setField(service, "ENDPOINT_URL", "http://localhost:9191/payment/doPayment");
	}
		
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void saveOrderTest() throws JsonProcessingException {
		Order order = getTransactionRequest().getOrder();
	    Payment payment = getTransactionRequest().getPayment();
	    Payment paymentOrder = new Payment();
	    paymentOrder.setOrderId(order.getId());
	    paymentOrder.setAmount(order.getPrice());
	    paymentOrder.setPaymentStatus("Success");
		
	    when(template.postForObject(Mockito.anyString(), Mockito.any(Payment.class), Mockito.any())).thenReturn(paymentOrder);
	    repository.save(Mockito.any(Order.class));
		TransactionResponse t= new TransactionResponse(order, payment.getAmount(), payment.getTransactionId(), "");
		TransactionResponse tr = service.saveOrder(getTransactionRequest());
		assertEquals(tr.getAmount(), t.getAmount());
	}

	private TransactionRequest getTransactionRequest() {
		Order order = getOrder();
        Payment payment = getPayment();
        
        TransactionRequest request = new TransactionRequest();
        request.setOrder(order);
        request.setPayment(payment);
		return request;		
	}
	
	private Order getOrder() {
		Order order = new Order();
		order.setId(101);
		order.setName("Item 1");
		order.setPrice(101);
		order.setQty(2);
		return order;
	}
	
	private Payment getPayment() {
		Payment payment = new Payment();
		payment.setPaymentId(100);
		payment.setAmount(101);
		payment.setPaymentStatus("Success");
		payment.setTransactionId(UUID.randomUUID().toString());
		return payment;
	}
}
