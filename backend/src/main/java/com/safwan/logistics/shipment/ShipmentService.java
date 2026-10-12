package com.safwan.logistics.shipment;

import com.safwan.logistics.order.Order;
import com.safwan.logistics.order.OrderNotFoundException;
import com.safwan.logistics.order.OrderRepository;
import com.safwan.logistics.order.OrderStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
private final ShipmentOutboxRepository outboxRepository;
    public ShipmentService(
            ShipmentRepository shipmentRepository,
            OrderRepository orderRepository,
            ShipmentOutboxRepository outboxRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public ShipmentResponse create(
            Long orderId,
            CreateShipmentRequest request
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (shipmentRepository.existsByOrder_Id(orderId)) {
            throw new ShipmentConflictException(
                    "Order already has a shipment"
            );
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new ShipmentConflictException(
                    "A shipment can only be created for a PROCESSING order"
            );
        }

        String carrier = request.carrier().strip();
        String trackingNumber = request.trackingNumber().strip();

        if (shipmentRepository.existsByCarrierAndTrackingNumber(
                carrier, trackingNumber
        )) {
            throw new ShipmentConflictException(
                    "Tracking number is already registered for this carrier"
            );
        }

        Shipment shipment = new Shipment(
                order,
                carrier,
                trackingNumber
        );

        order.changeStatus(OrderStatus.SHIPPED);

        Shipment savedShipment;

try {
    savedShipment = shipmentRepository.saveAndFlush(shipment);
} catch (DataIntegrityViolationException exception) {
    throw new ShipmentConflictException(
            "Shipment conflicts with existing database records"
    );
}

ShipmentEvent event = new ShipmentEvent(
        UUID.randomUUID().toString(),
        savedShipment.getId().toString(),
        OrderStatus.SHIPPED.name(),
        "UNKNOWN",
        Instant.now()
);

outboxRepository.save(new ShipmentOutboxEvent(event));

return ShipmentResponse.from(savedShipment);
    }



@Transactional(readOnly = true)
public ShipmentResponse findByOrderId(Long orderId) {
    if (!orderRepository.existsById(orderId)) {
        throw new OrderNotFoundException(orderId);
    }

    Shipment shipment = shipmentRepository.findByOrder_Id(orderId)
            .orElseThrow(() -> new ShipmentNotFoundException(orderId));

    return ShipmentResponse.from(shipment);
}

    
}