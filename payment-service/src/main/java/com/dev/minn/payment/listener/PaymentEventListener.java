package com.dev.minn.payment.listener;

import com.dev.minn.common.messaging.dto.MessageEnvelope;
import com.dev.minn.common.messaging.entity.ProcessedEvent;
import com.dev.minn.common.messaging.repository.ProcessedEventRepository;
import com.dev.minn.payment.config.RabbitMQConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentEventListener {

    ProcessedEventRepository processedEventRepository;
    ObjectMapper objectMapper;
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_CMD_QUEUE)
    public void handlePaymentCommand(MessageEnvelope<JsonNode> rawEnvelope) {
        log.info("Receive event: {}", rawEnvelope.getMessageType());
        if (processedEventRepository.existsByMessageIdAndConsumerName(rawEnvelope.getMessageId(), "PaymentListener")) {
            log.warn("Message {} has already been processed", rawEnvelope.getMessageId());
            return;
        }
        log.info("Giả lập link thanh toán");
        processedEventRepository.save(ProcessedEvent.builder()
                .messageId(rawEnvelope.getMessageId())
                .consumerName("PaymentListener")
                .build());
    }
}
