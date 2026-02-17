package com.payment.platform.authorization.controller;

import com.payment.platform.core.events.PaymentInitiatedEvent;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/authorization")
public class AuthorizationController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthorizationController.class);
    
    @Autowired
    private EventBus eventBus;
    
    @PostMapping("/test")
    public String testAuthorization(@RequestBody TestPaymentRequest request) {
        log.info("Received payment authorization test request for orderId: {}", request.getOrderId());
        log.debug("Request details - amount: {}, currency: {}, userId: {}, merchantId: {}, paymentMethod: {}", 
                 request.getAmount(), request.getCurrency(), request.getUserId(), 
                 request.getMerchantId(), request.getPaymentMethod());
        
        // Create a test payment initiation event
        PaymentInitiatedEvent event = new PaymentInitiatedEvent(
            UUID.randomUUID().toString(),
            request.getOrderId(),
            request.getAmount(),
            request.getCurrency(),
            request.getUserId(),
            request.getMerchantId(),
            request.getPaymentMethod()
        );
        
        log.info("Created PaymentInitiatedEvent with paymentId: {}", event.getPaymentId());
        
        // Publish event to Axon (this will trigger AuthorizationEventHandler)
        eventBus.publish(GenericEventMessage.asEventMessage(event));
        
        log.info("Published PaymentInitiatedEvent to Axon EventBus for paymentId: {}", event.getPaymentId());
        
        return "Payment authorization initiated for paymentId: " + event.getPaymentId();
    }
    
    public static class TestPaymentRequest {
        private String orderId;
        private String amount;
        private String currency;
        private String userId;
        private String merchantId;
        private String paymentMethod;
        
        // Getters and setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getMerchantId() { return merchantId; }
        public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
}
