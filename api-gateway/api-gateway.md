1. Cách để truyền thông tin header lấy thông tin từ jwt xuống các service khác  
2. // inject headers for downstream services
   ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
   .header("X-Username", grpcResponse.getUsername())
   .header("X-User-Id", String.valueOf(grpcResponse.getUserId()))
   .build();

                log.info("Request valid");
                return chain.filter(exchange.mutate().request(mutatedRequest).build()).then(Mono.fromRunnable(() -> {
                }));