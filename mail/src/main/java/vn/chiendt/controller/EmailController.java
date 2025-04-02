package vn.chiendt.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.chiendt.service.MailService;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {

    private final MailService mailService;

    @Operation(summary = "Send simple email", description = "Send email with plain text")
    @GetMapping("/send")
    public String sendEmail (@RequestParam String toEmail, @RequestParam String subject , @RequestParam String body){
        return mailService.sendEmail(toEmail,subject,body);
    }
}
