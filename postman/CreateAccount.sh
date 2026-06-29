curl --location 'http://localhost:8080/api/v1/accounts' \
--header 'idempotency-Key: 8157a8da-7fb5-4544-925f-8c7393a3fd16' \
--header 'Content-Type: application/json' \
--data '{
    "document_number": 73284915664
}'