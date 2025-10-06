


##1. Cách lấy thông tin từ một custom claim trong jwt tham khảo hàm public Long extractUserId(String token, TokenType type)

```java
 // inject headers for downstream services
ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
        .header("X-Username", grpcResponse.getUsername())
        .header("X-User-Id", String.valueOf(grpcResponse.getUserId()))
        .build();

chain.filter(exchange.mutate().request(mutatedRequest).build()).then(Mono.fromRunnable(() -> {}))
                
```