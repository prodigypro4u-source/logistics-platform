package com.safwan.logistics.order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false, length = 120)
    private String customerName;

    @Column(name = "delivery_address", nullable = false, length = 500)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Order() {
        // Required by JPA when loading records.
    }
    public void changeStatus(OrderStatus nextStatus) {
    if (nextStatus == null) {
        throw new IllegalArgumentException("Status is required");
    }

    if (status == nextStatus) {
        return;
    }

    boolean allowed = switch (status) {
        case CREATED ->
                nextStatus == OrderStatus.PROCESSING
                        || nextStatus == OrderStatus.CANCELLED;

        case PROCESSING ->
                nextStatus == OrderStatus.SHIPPED
                        || nextStatus == OrderStatus.CANCELLED;

        case SHIPPED ->
                nextStatus == OrderStatus.DELIVERED;

        case DELIVERED, CANCELLED -> false;
    };

    if (!allowed) {
        throw new InvalidOrderStatusException(status, nextStatus);
    }

    this.status = nextStatus;
}

    public Order(String customerName, String deliveryAddress) {
        this.customerName = customerName;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.CREATED;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
