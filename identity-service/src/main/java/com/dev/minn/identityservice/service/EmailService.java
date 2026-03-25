package com.dev.minn.identityservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {

    @NonFinal
    @Value("${spring.mail.username}")
    String FROM_EMAIL;

    JavaMailSender mailSender;

    @NonFinal
    Map<String, String> emailTemplates = new HashMap<>();

    @PostConstruct
    public void init() {
        loadTemplate("otp", "templates/registration-verify-email.html");
        loadTemplate("welcome", "templates/welcome-email.html");
    }

    private void loadTemplate(String key, String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            emailTemplates.put(key, content);
            log.info("Đã nạp template [{}] vào bộ nhớ.", key);
        } catch (Exception e) {
            log.error("Không thể đọc file template: {}", path, e);
        }
    }

    public void sendTemplatedEmail(String toEmail, String subject, String templateKey, Map<String, String> placeholders) {
        try {
            String htmlContent = emailTemplates.get(templateKey);
            if (htmlContent == null) {
                throw new IllegalArgumentException("Template không tồn tại: " + templateKey);
            }

            // Tự động tìm và thay thế tất cả các biến {{key}} bằng giá trị tương ứng
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                htmlContent = htmlContent.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_EMAIL);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Đã gửi email [{}] thành công tới: {}", templateKey, toEmail);

        } catch (Exception e) {
            log.error("Lỗi hạ tầng khi gửi email tới {}: {}", toEmail, e.getMessage());
        }
    }
}