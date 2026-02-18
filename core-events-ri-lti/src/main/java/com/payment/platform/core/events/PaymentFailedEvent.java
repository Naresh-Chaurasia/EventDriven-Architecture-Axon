package com.payment.platform.core.events;

import java.time.LocalDateTime;

/**
 * Event fired when payment settlement fails after all retry attempts.
 * 
 * This event represents the final failure state of a payment after
 * the settlement service has exhausted all retry attempts with the
 * payment provider. It contains detailed failure information for
 * audit trails and compensation workflows.
 * 
 * Used by: SettlementService to notify OrderService and other downstream
 *          services about payment settlement failures.
 * 
 * Triggers: Compensation workflows, customer notifications, manual review.
 */
public class PaymentFailedEvent extends PaymentEvent {
    
    private final String failureReason;
    private final String errorCode;
    private final int retryCount;
    private final LocalDateTime failureTimestamp;
    
    public PaymentFailedEvent(String paymentId, String orderId, 
                             String failureReason, String errorCode, 
                             int retryCount) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.failureReason = failureReason;
        this.errorCode = errorCode;
        this.retryCount = retryCount;
        this.failureTimestamp = LocalDateTime.now();
        this.timestamp = LocalDateTime.now();
    }
    
    public String getFailureReason() {
        return failureReason;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public LocalDateTime getFailureTimestamp() {
        return failureTimestamp;
    }
    
    @Override
    public String toString() {
        return "PaymentFailedEvent{" +
                "paymentId='" + getPaymentId() + '\'' +
                ", orderId='" + getOrderId() + '\'' +
                ", failureReason='" + failureReason + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", retryCount=" + retryCount +
                ", failureTimestamp=" + failureTimestamp +
                '}';
    }
}
