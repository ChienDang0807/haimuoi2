# Cart Service - Hướng Phát Triển

## Tổng Quan
Cart Service là một microservice quản lý giỏ hàng trong hệ thống e-commerce, được xây dựng bằng Spring Boot và MongoDB. Service này chịu trách nhiệm quản lý trạng thái giỏ hàng của người dùng, bao gồm thêm, sửa, xóa sản phẩm và các tính năng liên quan.

## Kiến Trúc Hiện Tại
- **Framework**: Spring Boot 3.x với Java 17
- **Database**: MongoDB
- **Documentation**: OpenAPI/Swagger
- **Monitoring**: Spring Boot Actuator
- **Validation**: Spring Boot Validation

## Hướng Phát Triển

### 1. Giai Đoạn 1: Hoàn Thiện Core Features (Q1 2024)

#### 1.1 Triển Khai Business Logic Cơ Bản
- **Mục tiêu**: Hoàn thiện các chức năng cơ bản của giỏ hàng
- **Công việc**:
  - Triển khai đầy đủ logic cho `CartService` và `CartItemService`
  - Xây dựng REST API endpoints cho tất cả operations
  - Implement validation cho các request/response
  - Thêm error handling và exception management

#### 1.2 Tích Hợp Với Các Service Khác
- **Mục tiêu**: Kết nối với product-service và user-service
- **Công việc**:
  - Tích hợp với Product Service để lấy thông tin sản phẩm
  - Tích hợp với User Service để xác thực người dùng
  - Implement circuit breaker pattern cho resilience
  - Thêm caching cho thông tin sản phẩm

#### 1.3 Testing & Quality Assurance
- **Mục tiêu**: Đảm bảo chất lượng code và tính ổn định
- **Công việc**:
  - Viết unit tests cho tất cả services
  - Viết integration tests cho API endpoints
  - Setup code coverage reporting
  - Implement contract testing với các service khác

### 2. Giai Đoạn 2: Nâng Cao Tính Năng (Q2 2024)

#### 2.1 Tính Năng Giỏ Hàng Nâng Cao
- **Mục tiêu**: Thêm các tính năng phức tạp hơn cho giỏ hàng
- **Công việc**:
  - Implement wishlist functionality
  - Thêm tính năng save for later
  - Implement cart sharing giữa các devices
  - Thêm tính năng cart abandonment recovery
  - Implement bulk operations (add multiple items)

#### 2.2 Pricing & Discount Engine
- **Mục tiêu**: Xây dựng hệ thống tính giá và giảm giá
- **Công việc**:
  - Implement dynamic pricing calculation
  - Thêm support cho multiple discount types (percentage, fixed, BOGO)
  - Implement coupon system integration
  - Thêm tính năng loyalty points
  - Implement tax calculation

#### 2.3 Multi-tenant Support
- **Mục tiêu**: Hỗ trợ nhiều shop/merchant
- **Công việc**:
  - Implement shop-based cart separation
  - Thêm shop-specific pricing rules
  - Implement cross-shop cart functionality
  - Thêm shop-specific discount policies

### 3. Giai Đoạn 3: Performance & Scalability (Q3 2024)

#### 3.1 Caching Strategy
- **Mục tiêu**: Tối ưu hóa performance thông qua caching
- **Công việc**:
  - Implement Redis caching cho cart data
  - Thêm distributed caching strategy
  - Implement cache invalidation policies
  - Thêm cache warming strategies

#### 3.2 Database Optimization
- **Mục tiêu**: Tối ưu hóa database performance
- **Công việc**:
  - Implement database indexing strategy
  - Thêm database sharding cho large scale
  - Implement read replicas
  - Thêm database connection pooling optimization

#### 3.3 Event-Driven Architecture
- **Mục tiêu**: Chuyển sang kiến trúc event-driven
- **Công việc**:
  - Implement event sourcing cho cart state
  - Thêm message queues (Kafka/RabbitMQ)
  - Implement event-driven cart synchronization
  - Thêm event replay capabilities

### 4. Giai Đoạn 4: Advanced Features (Q4 2024)

#### 4.1 AI/ML Integration
- **Mục tiêu**: Thêm trí tuệ nhân tạo vào cart service
- **Công việc**:
  - Implement recommendation engine
  - Thêm personalized product suggestions
  - Implement cart abandonment prediction
  - Thêm dynamic pricing based on ML

#### 4.2 Analytics & Reporting
- **Mục tiêu**: Cung cấp insights về cart behavior
- **Công việc**:
  - Implement cart analytics dashboard
  - Thêm real-time cart metrics
  - Implement cart conversion tracking
  - Thêm A/B testing framework

#### 4.3 Advanced Security
- **Mục tiêu**: Tăng cường bảo mật cho cart service
- **Công việc**:
  - Implement rate limiting
  - Thêm fraud detection
  - Implement cart data encryption
  - Thêm audit logging

### 5. Giai Đoạn 5: Platform & DevOps (Q1 2025)

#### 5.1 Container & Orchestration
- **Mục tiêu**: Chuẩn bị cho production deployment
- **Công việc**:
  - Optimize Docker images
  - Implement Kubernetes deployment
  - Thêm auto-scaling policies
  - Implement health checks và monitoring

#### 5.2 CI/CD Pipeline
- **Mục tiêu**: Tự động hóa quá trình development và deployment
- **Công việc**:
  - Setup automated testing pipeline
  - Implement blue-green deployment
  - Thêm automated rollback capabilities
  - Implement feature flags

#### 5.3 Monitoring & Observability
- **Mục tiêu**: Đảm bảo visibility vào system performance
- **Công việc**:
  - Implement distributed tracing
  - Thêm metrics collection (Prometheus)
  - Implement centralized logging (ELK stack)
  - Thêm alerting system

## Công Nghệ Dự Kiến Sử Dụng

### Backend Technologies
- **Spring Boot 3.x** - Core framework
- **Spring Data MongoDB** - Database access
- **Spring Cloud** - Microservices patterns
- **Redis** - Caching layer
- **Kafka/RabbitMQ** - Message queuing

### Infrastructure
- **Docker** - Containerization
- **Kubernetes** - Orchestration
- **Prometheus + Grafana** - Monitoring
- **ELK Stack** - Logging
- **Jenkins/GitHub Actions** - CI/CD

### Testing
- **JUnit 5** - Unit testing
- **TestContainers** - Integration testing
- **WireMock** - Service mocking
- **Pact** - Contract testing

## Metrics & KPIs

### Performance Metrics
- **Response Time**: < 100ms cho 95% requests
- **Throughput**: > 10,000 requests/second
- **Availability**: 99.9% uptime
- **Error Rate**: < 0.1%

### Business Metrics
- **Cart Conversion Rate**: Tỷ lệ chuyển đổi từ cart sang order
- **Cart Abandonment Rate**: Tỷ lệ bỏ giỏ hàng
- **Average Cart Value**: Giá trị trung bình giỏ hàng
- **Cart Recovery Rate**: Tỷ lệ khôi phục giỏ hàng bị bỏ

## Risk Management

### Technical Risks
- **Database Performance**: Risk về performance khi scale
- **Data Consistency**: Risk về consistency trong distributed system
- **Service Dependencies**: Risk về dependency failures

### Mitigation Strategies
- **Performance Testing**: Regular load testing
- **Circuit Breakers**: Implement resilience patterns
- **Monitoring**: Proactive monitoring và alerting
- **Backup Strategies**: Data backup và disaster recovery

## Team Structure

### Development Team
- **Backend Developers**: 2-3 developers
- **DevOps Engineer**: 1 engineer
- **QA Engineer**: 1 engineer
- **Product Owner**: 1 person

### Skills Required
- **Java/Spring Boot**: Core development skills
- **MongoDB**: Database expertise
- **Microservices**: Architecture knowledge
- **DevOps**: CI/CD và infrastructure
- **Testing**: Automated testing skills

## Budget Estimation

### Infrastructure Costs
- **Development Environment**: $500/month
- **Staging Environment**: $1,000/month
- **Production Environment**: $3,000/month

### Tool & Service Costs
- **Monitoring Tools**: $200/month
- **CI/CD Tools**: $300/month
- **Security Tools**: $400/month

## Success Criteria

### Technical Success
- ✅ All core features implemented và tested
- ✅ Performance targets met
- ✅ Security requirements satisfied
- ✅ Monitoring và alerting in place

### Business Success
- ✅ Improved cart conversion rate
- ✅ Reduced cart abandonment
- ✅ Better user experience
- ✅ Scalable architecture for future growth

---
## Một vài case xử lý
- Với mỗi sản phẩm nếu cùng Id nhưng khác attributes thì không đc cộng quantity mà phải thành thêm 1 CartItem mới
- Thay đổi cấu trúc bảng dễ hiểu dễ sử dụng hơn 
- **Cấu trúc bảng cũ**
```sql
-- Bảng cart_item
CREATE TABLE cart_item (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT,
    cart_id VARCHAR(255),
    cart_token VARCHAR(255),
    product_name VARCHAR(255),
    price NUMERIC(19, 2),
    quantity INTEGER,
    discount NUMERIC(19, 2),
    unit VARCHAR(100),
    shop_id VARCHAR(255),
    attributes JSONB
);
-- Bảng cart
CREATE TABLE cart (
    id VARCHAR(255) PRIMARY KEY,
    user_id BIGINT,
    cart_state VARCHAR(20) NOT NULL,
    shipping_fee NUMERIC(19, 2) DEFAULT 30000,
    discount NUMERIC(19, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
- **Cấu trúc bảng mới** 
```sql
-- Bảng Cart
CREATE TABLE tbl_cart (
    id VARCHAR(255) PRIMARY KEY,
    user_id BIGINT NULL,           -- NULL cho guest cart
    cart_token VARCHAR(255) NULL,  -- NULL cho user cart
    cart_state VARCHAR(20) NOT NULL,
    shipping_fee DECIMAL(10,2),
    discount DECIMAL(10,2),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Bảng CartItem
CREATE TABLE tbl_cart_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_identifier VARCHAR(255) NOT NULL,  -- Unified field
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255),
    price DECIMAL(10,2),
    quantity INTEGER,
    discount DECIMAL(10,2),
    unit VARCHAR(50),
    shop_id VARCHAR(255),
    attributes JSONB,
    
    -- Indexes
    INDEX idx_cart_identifier (cart_identifier),
    INDEX idx_cart_type (cart_type),
    INDEX idx_product_id (product_id)
);
```
- Luồng khi merge giỏ hàng trường hợp người dùng có giỏ hàng rôì:
  - Cart: Xóa cartToken, cập nhật userId 
  - CartItem: thay đổi cart_identifier = cartId + nếu sản phẩm đó có trong 
  giỏ rồi ( tức có cùng productId và attributes) thì cộng quantity nếu chưa thì tạo CartItem mới
- Luồng khi merge giỏ hàng trường hợp người dùng chưa có giỏ hàng:
  - Cart: Xóa cartToken, cập nhật userId
  - CartItem: chỉ lưu vào db
- Luồng khi thêm sản phẩm vào giỏ hàng 
  - Nếu chưa đăng nhập: Kiểm tra xem có giỏ chưa nếu chưa thì set cartId và cartToken bằng token trong cookie, userId = 0 các sản phẩm đc thêm vào set cart_identifier = token trong cookie
    nếu sản phẩm đó có trong giỏ rồi ( tức có cùng productId và attributes) thì cộng quantity nếu chưa thì tạo cart_item mới
  - Nếu đăng nhập: Khi thêm sản phẩm, cart_identifier = cart_id  nếu sản phẩm đó có trong
    giỏ rồi ( tức có cùng productId và attributes) thì cộng quantity nếu chưa thì tạo CartItem mới
- Luồng khi xóa sản phẩm: Xóa theo cart_identifier
- Luồng lấy sản phẩm: 
  - Đăng nhập : tức cartToken = null và userId != null lấy tất cả sản phẩm có cartId = cart_indentifier
  - Chưa đăng nhập: khi cartToken != null và userId = null lấy tất cả sản phẩm 
  