package vn.chiendt.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j(topic = "KAFKA-PRODUCER")
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.topic}")
    private String sendEmailTopic;

    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        log.info("Creating producer factory");

        Map<String, Object> props = new HashMap<>();
        // props.put(ProducerConfig.ACKS_CONFIG, "all" ); // default: 1
        // props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864L); // default: 32MB
        // props.put(ProducerConfig.BATCH_SIZE_CONFIG , 65536 ); //default: 16kb
        // props.put(ProducerConfig.LINGER_MS_CONFIG, 10 ); //default: 0 ( cu co message la gui )
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        if (profile.equals("prod")) {
            props.put("security.protocol", "SSL");
            props.put("ssl.truststore.type", "none");
            props.put("endpoint.identification.algorithm", "");
        }

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        log.debug("Creating Kafka template");
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic topic1() {
        return new NewTopic(sendEmailTopic, 3, (short) 1);
    }
}
