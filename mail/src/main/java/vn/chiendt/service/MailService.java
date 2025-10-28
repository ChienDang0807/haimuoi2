package vn.chiendt.service;

import com.google.gson.Gson;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final SendGrid sendGrid;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.sendgrid.template-id}")
    private String templateId;



    public String sendEmail(String toEmail, String subject, String body) {
        Email from = new Email("chien.haui0807@gmail.com", "Chien Dang"); // Email của bạn
        Email to = new Email(toEmail);

        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            // Kiểm tra kết quả phản hồi từ SendGrid
            if (response.getStatusCode() == 202) {
                return "Email sent successfully!";
            } else {
                return "Failed to send email: " + response.getBody();
            }

        } catch (IOException e) {
            return "Error occurred while sending email: " + e.getMessage();
        }
    }

    @KafkaListener(topics = "send-email-register-topic", groupId = "send-email-register-group")
    public void sendConfirmEmail (String message) throws IOException {
        log.info("Send email confirm user: {}", message);

        MessageDTO messageDTO = new Gson().fromJson(message, MessageDTO.class);
        Email from = new Email("chien.haui0807@gmail.com", "ChienDang ");
        Email to  = new Email(messageDTO.getEmail());

        String subject = "Confirm email account";

        Map<String, String> dynamicTemplate = new HashMap<>();
        dynamicTemplate.put("name",messageDTO.getUsername());
        dynamicTemplate.put("verification_link","http://localhost:4953/account/user/confirm-email?secretCode="+ messageDTO.getSecretCode());

        // mail server
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        dynamicTemplate.forEach(personalization::addDynamicTemplateData);

        mail.addPersonalization(personalization);
        mail.setTemplateId(templateId);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        if(response.getStatusCode() == 202){
            log.info("Email sent successfully");
        }else {
            log.error("Failed send email to: {}",to);
        }
    }

    @KafkaListener(topics ="${spring.kafka.consumer.topic-name-send-email-remind-pay-installment}" ,groupId = "send-email-notify-customer")
    public void send (String message) throws IOException {
        // handle send email pay installment
    }
    @Getter
    @Setter
    private static class MessageDTO{
        private Long id;
        private String email;
        private String username;
        private String secretCode;

    }
}
