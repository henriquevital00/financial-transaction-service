curl --location 'http://localhost:8080/api/v1/transactions' \
--header 'idempotency-Key: b41ba96d-f8dd-4b60-b5d5-791d1b8bbf74' \
--header 'Content-Type: application/json' \
--data '{
    "account_id": "448c5b2a-5c20-4597-8146-b55451433e95",
    "operation_type_id": 2,
    "amount": 123.45
}'