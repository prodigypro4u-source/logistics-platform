package com.safwan.logistics.shipment;

import com.safwan.logistics.order.Order;
import com.safwan.logistics.order.OrderRepository;
import com.safwan.logistics.order.OrderStatus;


import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ShipmentServiceTest {

    @Test
    void createdOrderCannotHaveShipment() {
        // Arrange: create repositories with controlled behavior.
        OrderRepository orderRepository =
                mock(OrderRepository.class);

        ShipmentRepository shipmentRepository =
                mock(ShipmentRepository.class);
               ShipmentOutboxRepository outboxRepository =
        mock(ShipmentOutboxRepository.class);
                

        ShipmentService service = new ShipmentService(
                shipmentRepository,
                orderRepository,
                outboxRepository
        );

        Order order = new Order("Safwan", "123 Main Street");

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(shipmentRepository.existsByOrder_Id(1L))
                .thenReturn(false);

        CreateShipmentRequest request =
                new CreateShipmentRequest("UPS", "TEST-001");

        // Act and assert: creation must be rejected.
        assertThrows(
                ShipmentConflictException.class,
                () -> service.create(1L, request)
        );

        assertEquals(OrderStatus.CREATED, order.getStatus());

        verify(shipmentRepository, never())
                .saveAndFlush(any(Shipment.class));
    }
}