package com.payment.platform.authorization.controller;

import com.payment.platform.core.events.PaymentInitiatedEvent;
import com.payment.platform.core.events.PaymentRejectedEvent;
import com.payment.platform.core.events.PaymentAuthorizedEvent;
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
    
    @PostMapping("/reject")
    public String testRejection(@RequestBody TestPaymentRequest request) {
        log.info("Simulating payment rejection for orderId: {}", request.getOrderId());
        
        // Create a PaymentRejectedEvent directly
        PaymentRejectedEvent rejectedEvent = new PaymentRejectedEvent(
            UUID.randomUUID().toString(),
            request.getOrderId(),
            "High risk score: Insufficient credit history",
            "RISK_REJECTION"
        );
        
        // Publish the rejection event
        eventBus.publish(GenericEventMessage.asEventMessage(rejectedEvent));
        
        log.info("Published PaymentRejectedEvent for paymentId: {}", rejectedEvent.getPaymentId());
        
        return "Payment rejected for paymentId: " + rejectedEvent.getPaymentId();
    }
    
    @PostMapping("/test-settlement-failure")
    public String testSettlementFailure(@RequestBody TestPaymentRequest request) {
        log.info("Testing settlement failure scenario for orderId: {}", request.getOrderId());
        
        // Create a PaymentAuthorizedEvent directly to trigger settlement processing
        // This bypasses the authorization rules and goes straight to settlement
        PaymentAuthorizedEvent authorizedEvent = new PaymentAuthorizedEvent(
            UUID.randomUUID().toString(),
            request.getOrderId(),
            "AUTH_" + System.currentTimeMillis(),
            "75", // moderate risk score as string
            request.getAmount()
        );
        
        eventBus.publish(GenericEventMessage.asEventMessage(authorizedEvent));
        log.info("Published PaymentAuthorizedEvent for settlement failure test: {}", authorizedEvent.getPaymentId());
        
        return "Settlement failure test initiated for paymentId: " + authorizedEvent.getPaymentId() + 
               ". Configure settlement service with failure settings to test.";
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
