package com.sumit.os.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumit.os.api.common.Payment;
import com.sumit.os.api.common.TransactionRequest;
import com.sumit.os.api.common.TransactionResponse;
import com.sumit.os.api.entity.Order;
import com.sumit.os.api.repository.OrderRepository;

@Service
@RefreshScope
public class OrderService {

    Logger logger= LoggerFactory.getLogger(OrderService.class);
    
    @Autowired
    private OrderRepository repository;
    
    @Autowired
    @Lazy
    private RestTemplate template;

    @Value("${microservice.payment-service.endpoints.endpoint.uri}")
    private String ENDPOINT_URL;
   
    public TransactionResponse saveOrder(TransactionRequest request) throws JsonProcessingException {
        String response = "";
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());
        
        //Rest call
        logger.info("Order-Service Request : {}", new ObjectMapper().writeValueAsString(request));
        logger.info("ENDPOINT_URL : "+ ENDPOINT_URL);
        Payment paymentResponse = null;
        if(template != null) {
        	paymentResponse = template.postForObject(ENDPOINT_URL, payment, Payment.class);
        }else {
        	logger.error("Rest template is not available....");	
        }
        //Payment paymentResponse = template.postForObject("http://localhost:9191/payment/doPayment", payment, Payment.class);
        //Payment paymentResponse = template.postForObject("http://PAYMENT-SERVICE/payment/doPayment", payment, Payment.class);
        
        logger.info("Payment-Service response from Order-Service rest call : {}", new ObjectMapper().writeValueAsString(paymentResponse));
        response = paymentResponse!= null ? (paymentResponse.getPaymentStatus().equals("success") ? "payment processing successful and order placed" : "there is a failure in payment api , order added to cart"): "Technical error....";
        logger.info("Order Service getting Response from Payment-Service : "+new ObjectMapper().writeValueAsString(response));
        repository.save(order);
        return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(), response);
    }
}
