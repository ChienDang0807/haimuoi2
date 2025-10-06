# PRODUCT SERVICE - V2


## 1. Sync ES Postgres qua Avro Kafka

**Các bước triển khai:**
```text
1. Thêm file định nghĩa avro : src/main/avro
2. Thêm file dependency 
3. Thêm plugin trong file POM
4. Thêm đường dẫn Schema Registry
5. Định nghĩa cấu hình trong Producer và Consumer
```

**File .avsc**
```text
{
  "type": "record",
  "name": "ProductEvent",
  "namespace": "com.example.avro",
  "fields": [
    { "name": "eventType", "type": { "type": "enum", "name": "ProductEventType", "symbols": ["CREATED", "UPDATED", "DELETED"] } },
    { "name": "id", "type": "long" },
    { "name": "slug", "type": ["null", "string"], "default": null },
    { "name": "name", "type": ["null", "string"], "default": null },
    { "name": "description", "type": ["null", "string"], "default": null },
    {
      "name": "price",
      "type": {
        "type": "bytes",
        "logicalType": "decimal",
        "precision": 12,
        "scale": 2
      }
    },
    { "name": "userId", "type": ["null", "long"], "default": null },
    { "name": "status", "type": { "type": "enum", "name": "ProductStatus", "symbols": ["DRAFT", "ACTIVE", "INACTIVE", "DELETED"] } },
    { "name": "attributes", "type": ["null", "string"], "default": null },
    { "name": "timestamp", "type": "long" },
    { "name": "createdAt", "type": ["null", "long"], "default": null },
    { "name": "updatedAt", "type": ["null", "long"], "default": null }
  ]
}
```

**Dependency**
```xml
<dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-avro-serializer</artifactId>
    <version>7.7.1</version>
</dependency>

<dependency>
    <groupId>org.apache.avro</groupId>
    <artifactId>avro</artifactId>
    <version>1.12.0</version>
</dependency>

<!-- Mock Schema Registry -->
<dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-schema-registry-client</artifactId>
    <version>7.7.1</version> <!-- chỉ dùng cho dev/test -->
</dependency>
```

**Định nghĩa plugin** 
```xml
<repositories>
    <repository>
        <id>confluent</id>
        <url>https://packages.confluent.io/maven/</url>
    </repository>
</repositories>
<build>
    <plugins>
        <!-- for avro-->
        <plugin>
            <groupId>org.apache.avro</groupId>
            <artifactId>avro-maven-plugin</artifactId>
            <version>1.12.0</version>
            <executions>
                <execution>
                    <phase>generate-sources</phase>
                    <goals>
                        <goal>schema</goal>
                    </goals>
                    <configuration>
                        <sourceDirectory>${project.basedir}/src/main/avro</sourceDirectory>
                        <outputDirectory>${project.basedir}/target/generated-sources/avro</outputDirectory>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

**Cấu hình Producer**
```java
 @Bean
    public ProducerFactory<String, ProductEvent> producerFactory() {
        log.info("Creating producer factory");

        Map<String, Object> props = new HashMap<>();
        // props.put(ProducerConfig.ACKS_CONFIG, "all" ); // default: 1
        // props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864L); // default: 32MB
        // props.put(ProducerConfig.BATCH_SIZE_CONFIG , 65536 ); //default: 16kb
        // props.put(ProducerConfig.LINGER_MS_CONFIG, 10 ); //default: 0 ( cu co message la gui )
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        props.put("schema.registry.url", "mock://localhost");

        if (profile.equals("prod")) {
            props.put("security.protocol", "SSL");
            props.put("ssl.truststore.type", "none");
            props.put("endpoint.identification.algorithm", "");
        }

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, ProductEvent> kafkaTemplate() {
        log.debug("Creating Kafka template");
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic topic1() {
        return new NewTopic(productSyncEvents, 3, (short) 1);
    }
```


**Cấu hình Consumer**

```java
 @Bean
    public ConsumerFactory<String, ProductEvent> consumerFactory() {
        log.debug("Creating consumer factory");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);

        props.put("schema.registry.url", "mock://localhost");


        // Bật đọc theo class cụ thể (SpecificRecord)
        props.put("specific.avro.reader", true);

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
```

**Mock Registry**
```java
@Bean
public SchemaRegistryClient schemaRegistryClient() {
    return new MockSchemaRegistryClient();
}
// Trong trường hợp dùng SchemaRegistry server cần câú hình registry url trong producer và consumer config
```

**Lưu ý** 
```text
- Cần đồng bộ định nghĩa ở consumer/producer chẳng hạn trong quá trình chuyển từ ByteBuffer về BigDecimal (tham khảo common.AvroConvert)
```

## Vấn đề còn tồn tại 
- Chưa xử lí được addProduct khi một bước lỗi các third party vẫn thao tác dẫn đến dữ liệu ảo
- 