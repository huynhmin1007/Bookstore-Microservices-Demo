package com.dev.minn.notification.client;

import com.dev.minn.notification.client.dto.MailResponse;
import com.dev.minn.notification.client.dto.SendMailRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "email-client", url = "${client.email-client.base-url}")
public interface EmailClient {

    @PostMapping(
            value = "${client.email-client.send-email-path}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    MailResponse sendEmail(
            @RequestHeader("api-key") String apiKey,
            @Valid @RequestBody SendMailRequest request
    );
}
