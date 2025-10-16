# SHOP SERVICE - V1

## 1.Model
**Shop**
```sql
CREATE TABLE shop (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shop_name VARCHAR(255) NOT NULL ,
    slug VARCHAR(255),
    rating NUMERIC(2,1),
    address VARCHAR(255),
    description VARCHAR(255)
    
)
```

