package vn.chiendt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vn.chiendt.request.OneSignalNotificationRequest;


import java.util.List;
import java.util.Map;

@Service
@Slf4j(topic = "ONESIGNAL-SERVICE")
public class OneSignalService {
    private final WebClient webClient;

    @Value("${onesignal.app-id}")
    private String appId;

    @Value("${onesignal.rest-api-key}")
    private String restApiKey;

    public OneSignalService(WebClient.Builder webClientBuilder, @Value("${onesignal.api-url}") String oneSignalUrl) {
        this.webClient = webClientBuilder.baseUrl(oneSignalUrl).build();
    }

    public Mono<String> sendPushNotification(List<String> playerIds, String message) {
        log.info("sendPushNotification called, playerIds = {}, message = {}", playerIds, message);

        OneSignalNotificationRequest request = new OneSignalNotificationRequest();
        request.setAppId(appId);
        request.setPlayerIds(playerIds);
        request.setContents(Map.of("en", message));

        return webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Basic " + restApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class);
    }
}
