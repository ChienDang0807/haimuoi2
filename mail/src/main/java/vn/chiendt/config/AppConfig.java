package vn.chiendt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${spring.sendgrid.api-key}")
    private  String apiKey;
}
