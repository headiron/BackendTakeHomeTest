#!/bin/bash

BASE_URL=http://localhost:8080

echo "Creating sleep log..."

CREATE_RESPONSE=$(curl -s \
-X POST "$BASE_URL/sleep_log" \
-H "Content-Type: application/json" \
-d '{
"user_id":1,
"sleep_date":"2026-06-17",
"bed_time":"23:00",
"wake_up_time":"07:00",
"morning_feeling":"GOOD"
}')

echo "$CREATE_RESPONSE"

ID=$(echo "$CREATE_RESPONSE" | jq -r '.id')

echo "Created Sleep Log ID=$ID"

echo "Getting sleep log..."

curl -s \
"$BASE_URL/sleep_log/$ID"

echo ""

echo "Updating sleep log..."

curl -s \
-X PUT \
"$BASE_URL/sleep_log/$ID" \
-H "Content-Type: application/json" \
-d '{
"wake_up_time":"08:00",
"morning_feeling":"OK"
}'

echo ""

echo "Listing sleep logs..."

curl -s \
"$BASE_URL/sleep_log"

echo ""

echo "30 Days Average"
curl -s \
"$BASE_URL/sleep_log_stat"

echo ""

echo "Deleting sleep log..."

curl -s \
-X DELETE \
"$BASE_URL/sleep_log/$ID"

echo ""

echo "Done."