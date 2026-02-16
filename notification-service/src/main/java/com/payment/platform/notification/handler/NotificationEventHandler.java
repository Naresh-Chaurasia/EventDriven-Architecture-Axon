package com.payment.platform.notification.handler;

import com.payment.platform.notification.service.NotificationService;
import com.payment.platform.notification.service.ConsoleNotificationService;
import com.payment.platform.notification.model.NotificationEntity;
import com.payment.platform.notification.model.NotificationChannel;
import com.payment.platform.notification.model.NotificationStatus;
import com.payment.platform.core.events.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@ProcessingGroup("notification-group")
@Slf4j
public class NotificationEventHandler {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ConsoleNotificationService consoleNotificationService;
    
    @EventHandler
    public void on(PaymentSettledEvent event) {
        boolean useConsoleNotificationService = true;

        log.info("Processing PaymentSettledEvent for notification: {}", event.getPaymentId());
        
        if (useConsoleNotificationService) {
            // Use ConsoleNotificationService
            log.info("Using ConsoleNotificationService for payment: {}", event.getPaymentId());
            
            // Create notification entity for console service
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("paymentId", event.getPaymentId());
            notificationData.put("orderId", event.getOrderId());
            notificationData.put("settlementId", event.getSettlementId());
            notificationData.put("settlementDate", event.getSettlementDate());
            notificationData.put("amount", "Amount from payment");
            
            // Create notification entity
            NotificationEntity notification = createNotificationEntity(event, notificationData);
            
            consoleNotificationService.sendConsoleNotification(notification);
            log.info("Console notification sent for settled payment: {}", event.getPaymentId());
            
        } else {
            // Use current implementation (NotificationService)
            log.info("Using NotificationService for payment: {}", event.getPaymentId());
            
            // Create notification data
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("paymentId", event.getPaymentId());
            notificationData.put("orderId", event.getOrderId());
            notificationData.put("settlementId", event.getSettlementId());
            notificationData.put("settlementDate", event.getSettlementDate());
            notificationData.put("amount", "Amount from payment");

            // Send notification to user (assuming user ID can be derived from order ID)
            String recipient = "user@" + event.getOrderId() + ".com"; // Simplified recipient logic
            
            // Uncomment when ready to use NotificationService
            // notificationService.sendNotification(
            //     event.getPaymentId(), 
            //     "payment.settled", 
            //     recipient, 
            //     notificationData
            // );
            
            log.info("NotificationService implementation called for settled payment: {}", event.getPaymentId());
        }
        
        log.info("Notifications sent for settled payment: {}", event.getPaymentId());
    }
    
    private NotificationEntity createNotificationEntity(PaymentSettledEvent event, Map<String, Object> notificationData) {
        // Create a proper NotificationEntity for console service
        NotificationEntity notification = new NotificationEntity();
        notification.setCorrelationId(event.getPaymentId());
        notification.setEventType("PaymentSettledEvent");
        notification.setRecipient("user@" + event.getOrderId() + ".com");
        notification.setChannel(NotificationChannel.CONSOLE);
        notification.setStatus(NotificationStatus.SENT);
        notification.setSubject("Payment Settled Successfully");
        
        // Explicitly set createdAt since @PrePersist only works for database persistence
        notification.setCreatedAt(java.time.LocalDateTime.now());
        
        // Create content from notification data
        StringBuilder content = new StringBuilder();
        content.append("Payment Details:\n");
        content.append("- Payment ID: ").append(event.getPaymentId()).append("\n");
        content.append("- Order ID: ").append(event.getOrderId()).append("\n");
        content.append("- Settlement ID: ").append(event.getSettlementId()).append("\n");
        content.append("- Settlement Date: ").append(event.getSettlementDate()).append("\n");
        content.append("- Amount: ").append(notificationData.get("amount")).append("\n");
        
        notification.setContent(content.toString());
        
        // Set metadata as JSON string
        notification.setMetadata(notificationData.toString());
        
        return notification;
    }
    
    //@EventHandler
    public void on(PaymentRejectedEvent event) {
        log.info("Processing PaymentRejectedEvent for notification: {}", event.getPaymentId());
        
        // Create notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("paymentId", event.getPaymentId());
        notificationData.put("orderId", event.getOrderId());
        notificationData.put("reason", event.getRejectionReason());
        notificationData.put("errorCode", event.getErrorCode());
        notificationData.put("timestamp", event.getTimestamp());
        
        // Send notification to user
        String recipient = "user@" + event.getOrderId() + ".com"; // Simplified recipient logic
        
        notificationService.sendNotification(
            event.getPaymentId(), 
            "payment.rejected", 
            recipient, 
            notificationData
        );
        
        // Send alert to support team
        String supportRecipient = "support@payment-platform.com";
        notificationService.sendNotification(
            event.getPaymentId() + "-support", 
            "payment.rejected", 
            supportRecipient, 
            notificationData
        );
        
        log.info("Notifications sent for rejected payment: {}", event.getPaymentId());
    }
    
    //@EventHandler
    public void on(PaymentAuthorizedEvent event) {
        log.info("Processing PaymentAuthorizedEvent for notification: {}", event.getPaymentId());
        
        // Create notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("paymentId", event.getPaymentId());
        notificationData.put("orderId", event.getOrderId());
        notificationData.put("authorizationCode", event.getAuthorizationCode());
        notificationData.put("riskScore", event.getRiskScore());
        notificationData.put("amount", event.getAmount());
        
        // Send notification to user about authorization
        String recipient = "user@" + event.getOrderId() + ".com"; // Simplified recipient logic
        
        notificationService.sendNotification(
            event.getPaymentId(), 
            "payment.authorized", 
            recipient, 
            notificationData
        );
        
        log.info("Authorization notification sent: {}", event.getPaymentId());
    }
    
    //@EventHandler
    public void on(PaymentInitiatedEvent event) {
        log.info("Processing PaymentInitiatedEvent for notification: {}", event.getPaymentId());
        
        // Create notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("paymentId", event.getPaymentId());
        notificationData.put("orderId", event.getOrderId());
        notificationData.put("amount", event.getAmount());
        notificationData.put("currency", event.getCurrency());
        notificationData.put("userId", event.getUserId());
        notificationData.put("merchantId", event.getMerchantId());
        notificationData.put("paymentMethod", event.getPaymentMethod());
        
        // Send notification to user about payment initiation
        String recipient = "user@" + event.getUserId() + ".com"; // Simplified recipient logic
        
        notificationService.sendNotification(
            event.getPaymentId(), 
            "payment.initiated", 
            recipient, 
            notificationData
        );
        
        log.info("Payment initiation notification sent: {}", event.getPaymentId());
    }
    
    // Handle custom order events if they exist
   // @EventHandler
    public void on(Object event) {
        // Generic event handler for any other events
        if (event.getClass().getSimpleName().contains("Order")) {
            log.info("Processing order event for notification: {}", event.getClass().getSimpleName());
            
            // Extract basic information from the event
            String eventType = event.getClass().getSimpleName();
            String correlationId = eventType + "-" + System.currentTimeMillis();
            
            // Send generic order notification
            notificationService.sendNotification(
                correlationId,
                "order." + eventType.toLowerCase(),
                "admin@payment-platform.com",
                event
            );
        }
    }
}