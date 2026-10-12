package com.safwan.logistics.shipment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.concurrent.TimeUnit;

@Service
public class ShipmentOutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(ShipmentOutboxPublisher.class);

    private final ShipmentOutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    public ShipmentOutboxPublisher(
            ShipmentOutboxRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            JsonMapper jsonMapper) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        var pendingEvents = repository
                .findTop50ByPublishedFalseOrderByEventTimeAscEventIdAsc();

        for (ShipmentOutboxEvent pending : pendingEvents) {
            try {
                ShipmentEvent event = pending.toEvent();
                String json = jsonMapper.writeValueAsString(event);

                kafkaTemplate.send(
                        "shipment-events",
                        event.shipmentId(),
                        json
                ).get(20, TimeUnit.SECONDS);

                repository.markPublished(event.eventId());

                log.info(
                        "Published eventId={} shipmentId={} status={}",
                        event.eventId(),
                        event.shipmentId(),
                        event.status()
                );

            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                return;

            } catch (Exception error) {
                log.warn(
                        "Event {} remains pending; will retry",
                        pending.getEventId(),
                        error
                );
                break;
            }
        }
    }
}