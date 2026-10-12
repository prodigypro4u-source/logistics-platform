package com.safwan.logistics.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order newOrder() {
        return new Order("Safwan", "123 Main Street");
    }

    @Test
    void newOrderStartsAsCreated() {
        Order order = newOrder();

        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void orderCanCompleteDeliveryLifecycle() {
        Order order = newOrder();

        order.changeStatus(OrderStatus.PROCESSING);
        order.changeStatus(OrderStatus.SHIPPED);
        order.changeStatus(OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void createdOrderCanBeCancelled() {
        Order order = newOrder();

        order.changeStatus(OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void processingOrderCanBeCancelled() {
        Order order = newOrder();
        order.changeStatus(OrderStatus.PROCESSING);

        order.changeStatus(OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void orderCannotSkipProcessingAndShipping() {
        Order order = newOrder();

        assertThrows(
                InvalidOrderStatusException.class,
                () -> order.changeStatus(OrderStatus.DELIVERED)
        );

        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void shippedOrderCannotBeCancelled() {
        Order order = newOrder();
        order.changeStatus(OrderStatus.PROCESSING);
        order.changeStatus(OrderStatus.SHIPPED);

        assertThrows(
                InvalidOrderStatusException.class,
                () -> order.changeStatus(OrderStatus.CANCELLED)
        );

        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    void deliveredOrderCannotBeReopened() {
        Order order = newOrder();
        order.changeStatus(OrderStatus.PROCESSING);
        order.changeStatus(OrderStatus.SHIPPED);
        order.changeStatus(OrderStatus.DELIVERED);

        assertThrows(
                InvalidOrderStatusException.class,
                () -> order.changeStatus(OrderStatus.PROCESSING)
        );

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void cancelledOrderCannotBeReopened() {
        Order order = newOrder();
        order.changeStatus(OrderStatus.CANCELLED);

        assertThrows(
                InvalidOrderStatusException.class,
                () -> order.changeStatus(OrderStatus.PROCESSING)
        );

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void repeatingCurrentStatusIsAllowed() {
        Order order = newOrder();

        assertDoesNotThrow(
                () -> order.changeStatus(OrderStatus.CREATED)
        );

        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void nullStatusIsRejected() {
        Order order = newOrder();

        assertThrows(
                IllegalArgumentException.class,
                () -> order.changeStatus(null)
        );

        assertEquals(OrderStatus.CREATED, order.getStatus());
    }
}