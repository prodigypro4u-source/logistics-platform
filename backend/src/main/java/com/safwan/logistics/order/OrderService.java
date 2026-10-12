package com.safwan.logistics.order;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;



@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Order order = new Order(
                request.customerName().strip(),
                request.deliveryAddress().strip()
        );

        Order savedOrder = orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }
@Transactional(readOnly = true)
public OrderPageResponse findAll(
        int page,
        int size,
        OrderStatus status
) {
    if (page < 0) {
        throw new InvalidOrderQueryException(
                "Page must be zero or greater"
        );
    }

    if (size < 1 || size > 100) {
        throw new InvalidOrderQueryException(
                "Size must be between 1 and 100"
        );
    }

    PageRequest pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "id")
    );

   Page<Order> result;

if (status == null) {
    result = orderRepository.findAll(pageable);
} else {
    result = orderRepository.findByStatus(status, pageable);
}

    return new OrderPageResponse(
            result.getContent()
                    .stream()
                    .map(OrderResponse::from)
                    .toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages()
    );
}
@Transactional
public OrderResponse updateStatus(
        Long id,
        UpdateOrderStatusRequest request
) {
    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
            if (request.status() == OrderStatus.SHIPPED
        && order.getStatus() != OrderStatus.SHIPPED) {
    throw new InvalidOrderQueryException(
            "Create a shipment to mark an order as SHIPPED"
    );
}



            

    order.changeStatus(request.status());

    return OrderResponse.from(order);
}


    @Transactional(readOnly = true)
public OrderResponse findById(Long id) {
    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

    return OrderResponse.from(order);
}
}