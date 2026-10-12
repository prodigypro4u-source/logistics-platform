package com.safwan.logistics.shipment;

import com.safwan.logistics.order.Order;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "carrier", nullable = false, length = 100)
    private String carrier;

    @Column(name = "tracking_number", nullable = false, length = 120)
    private String trackingNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Shipment() {
        // Required by JPA.
    }

    public Shipment(Order order, String carrier, String trackingNumber) {
        this.order = order;
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}