curl -H "Content-Type: application/json" localhost:9999/transactions | jq
curl -H "Content-Type: application/json" -d "{\"description\":\"Iphone 13\",\"amount\":199.90}" -X POST localhost:9999/transactions | jq
