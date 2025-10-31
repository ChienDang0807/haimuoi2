# Cart Service

A microservice for managing shopping cart functionality in the Haimuoi2 e-commerce platform.

## Features

- **Cart Management**: Create, retrieve, update, and delete shopping carts
- **Item Management**: Add, update, remove items from cart
- **User Support**: Support for both authenticated users and anonymous users (via cart tokens)
- **Inventory Integration**: Check product availability and stock levels
- **Product Integration**: Fetch product details and pricing
- **Validation**: Comprehensive input validation and error handling

## API Endpoints

### Cart Operations
- `POST /api/v1/cart` - Create a new cart
- `GET /api/v1/cart/user/{userId}` - Get cart by user ID
- `GET /api/v1/cart/token/{cartToken}` - Get cart by token

### Cart Item Operations
- `POST /api/v1/cart/user/{userId}/items` - Add item to cart
- `POST /api/v1/cart/token/{cartToken}/items` - Add item to cart by token
- `PUT /api/v1/cart/user/{userId}/items/{productId}` - Update cart item
- `PUT /api/v1/cart/token/{cartToken}/items/{productId}` - Update cart item by token
- `DELETE /api/v1/cart/user/{userId}/items/{productId}` - Remove item from cart
- `DELETE /api/v1/cart/token/{cartToken}/items/{productId}` - Remove item from cart by token

### Cart Management
- `DELETE /api/v1/cart/user/{userId}` - Clear cart
- `DELETE /api/v1/cart/token/{cartToken}` - Clear cart by token
- `GET /api/v1/cart/user/{userId}/count` - Get cart items count
- `GET /api/v1/cart/token/{cartToken}/count` - Get cart items count by token
- `GET /api/v1/cart/user/{userId}/total` - Calculate cart total
- `GET /api/v1/cart/token/{cartToken}/total` - Calculate cart total by token

## Configuration

### Application Profiles

- **dev**: Development environment configuration
- **test**: Test environment configuration

### Database

The service uses PostgreSQL as the primary database with the following tables:
- `tbl_cart`: Stores cart information
- `tbl_cart_item`: Stores cart items

### External Services

- **Product Service**: For fetching product details
- **Inventory Service**: For checking stock availability

## Running the Service

1. **Prerequisites**:
   - Java 17+
   - Maven 3.6+
   - PostgreSQL 12+

2. **Database Setup**:
   ```sql
   CREATE DATABASE haimuoi2_cart;
   CREATE DATABASE haimuoi2_cart_test;
   ```

3. **Run the service**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access Swagger UI**:
   - Development: http://localhost:8090/swagger-ui.html

## Architecture

### Key Components

- **CartController**: REST API endpoints
- **CartService**: Business logic layer
- **CartRepository**: Data access layer for carts
- **CartItemRepository**: Data access layer for cart items
- **CartMapper**: Object mapping between entities and DTOs
- **GlobalExceptionHandler**: Centralized error handling

### Design Patterns

- **Repository Pattern**: For data access abstraction
- **Service Layer Pattern**: For business logic separation
- **DTO Pattern**: For data transfer between layers
- **Mapper Pattern**: For object transformation

## Error Handling

The service includes comprehensive error handling for:
- Validation errors (400 Bad Request)
- Cart not found (404 Not Found)
- Out of stock errors (400 Bad Request)
- Internal server errors (500 Internal Server Error)

## Testing

Run tests with:
```bash
mvn test
```

## Dependencies

- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL Driver
- MapStruct for object mapping
- Lombok for code generation
- OpenFeign for service communication
- Swagger/OpenAPI for documentation
