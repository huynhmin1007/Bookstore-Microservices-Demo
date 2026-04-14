package com.dev.minn.search.listener;

import com.dev.minn.common.event.EventEnvelope;
import com.dev.minn.common.strategy.EventHandlerStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventListener {

    List<EventHandlerStrategy> strategies;

    @RabbitListener(queues = "${app.rabbitmq.queue.high-priority}")
    public void receiveHighPriorityMessage(EventEnvelope envelope) {
        log.info("[High Priority Queue] Received event: {}", envelope.getEventType());
        processEvent(envelope);
    }

    @RabbitListener(
            queues = "${app.rabbitmq.queue.low-priority}",
            containerFactory = "batchRabbitListenerContainerFactory"
    )
    public void receiveLowPriorityMessage(List<EventEnvelope> envelopes) {
        log.info("🐢 [Low Queue] Vớt được lô {} events", envelopes.size());

        Map<String, List<EventEnvelope>> groupedEvents = envelopes.stream()
                .collect(Collectors.groupingBy(EventEnvelope::getEventType));

        groupedEvents.forEach((eventType, eventList) -> {
            try {
                EventHandlerStrategy strategy = strategies.stream()
                        .filter(s -> s.supports(eventType))
                        .findFirst()
                        .orElseThrow();

                strategy.handleBatch(eventList);

            } catch (Exception e) {
                log.error("Lỗi xử lý lô event {}: {}", eventType, e.getMessage());
                throw new AmqpRejectAndDontRequeueException("Thất bại khi xử lý lô", e);
            }
        });
    }

    private void processEvent(EventEnvelope envelope) {
        try {
            EventHandlerStrategy strategy = strategies.stream()
                    .filter(s -> s.supports(envelope.getEventType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Strategy cho Event: " + envelope.getEventType()));

            strategy.handle(envelope.getPayload());

        } catch (IllegalArgumentException e) {
            log.warn("Bỏ qua event chưa được hỗ trợ: {}", e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Event chưa được hỗ trợ", e);

        } catch (Exception e) {
            log.error("Lỗi hệ thống khi xử lý event {}: {}", envelope.getEventType(), e.getMessage());

            throw new AmqpRejectAndDontRequeueException("Thất bại khi xử lý event tại Search Service", e);
        }
    }
}