package vn.chiendt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import vn.chiendt.request.NotificationRequest;
import vn.chiendt.service.OneSignalService;

@RestController
@RequestMapping("/notifications")
@Slf4j(topic = "NOTIFICATION-CONTROLLER")
public class NotificationController {
    private final OneSignalService oneSignalService;

    public NotificationController(OneSignalService oneSignalService) {
        this.oneSignalService = oneSignalService;
    }

    @PostMapping("/send")
    public Mono<String> sendNotification(@RequestBody NotificationRequest request) {
        log.info("sendNotification called, request: {}", request);
        return oneSignalService.sendPushNotification(request.getPlayerIds(), request.getMessage());
    }
}
