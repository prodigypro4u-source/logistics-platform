package com.safwan.logistics.shipment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "shipment_event_outbox")
public class ShipmentOutboxEvent {

    @Id
    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "shipment_id", nullable = false)
    private String shipmentId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, columnDefinition = "text")
    private String location;

    @Column(name = "event_time", nullable = false)
    private Instant eventTime;

    @Column(nullable = false)
    private boolean published;

    protected ShipmentOutboxEvent() {
    }

    public ShipmentOutboxEvent(ShipmentEvent event) {
        this.eventId = event.eventId();
        this.shipmentId = event.shipmentId();
        this.status = event.status();
        this.location = event.location();
        this.eventTime = event.eventTime();
        this.published = false;
    }

    public String getEventId() {
        return eventId;
    }

    public ShipmentEvent toEvent() {
        return new ShipmentEvent(
                eventId,
                shipmentId,
                status,
                location,
                eventTime
        );
    }
}