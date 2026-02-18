#!/bin/bash

# Settlement Failure Testing Script
# This script demonstrates different settlement failure scenarios

echo "=== Settlement Failure Testing Script ==="
echo ""

# Base URL for API Gateway
BASE_URL="http://localhost:8010"

echo "Testing settlement failure scenarios..."
echo ""

# Test 1: Normal settlement (should succeed by default)
echo "1. Testing normal settlement (should succeed):"
curl --location "$BASE_URL/api/authorization/test-settlement-failure" \
--header 'Content-Type: application/json' \
--data '{
    "orderId": "order-normal-123",
    "amount": "100.00", 
    "currency": "USD",
    "userId": "user-123",
    "merchantId": "merchant-456",
    "paymentMethod": "CREDIT_CARD"
}' | jq '.' 2>/dev/null || echo "Response received"
echo ""
echo ""

# Test 2: Multiple settlement attempts to see random failures
echo "2. Testing multiple settlement attempts (configure 50% failure rate to see mixed results):"
for i in {1..3}; do
    echo "Attempt $i:"
    curl --location "$BASE_URL/api/authorization/test-settlement-failure" \
    --header 'Content-Type: application/json' \
    --data "{
        \"orderId\": \"order-test-$i\",
        \"amount\": \"50.00\", 
        \"currency\": \"USD\",
        \"userId\": \"user-$i\",
        \"merchantId\": \"merchant-456\",
        \"paymentMethod\": \"CREDIT_CARD\"
    }" | jq '.' 2>/dev/null || echo "Response received"
    echo ""
done

echo "=== Configuration Instructions ==="
echo "To enable settlement failure testing, edit:"
echo "  /settlement-service-ri/src/main/resources/application.properties"
echo ""
echo "For 100% failure rate:"
echo "  settlement.test.force-failure=true"
echo ""
echo "For 50% random failure rate:"
echo "  settlement.test.force-failure=false"
echo "  settlement.test.failure-rate=0.5"
echo ""
echo "For faster retry testing:"
echo "  settlement.retry.max-attempts=2"
echo "  settlement.retry.delay-ms=500"
echo ""
echo "Remember to restart the settlement service after changing configuration!"
echo ""

echo "=== Log Monitoring ==="
echo "Watch settlement service logs for:"
echo "  'TEST MODE: Forcing settlement failure'"
echo "  'Settlement attempt X failed for payment'"
echo "  'Settlement failed after X attempts'"
echo "  'Published PaymentFailedEvent'"
