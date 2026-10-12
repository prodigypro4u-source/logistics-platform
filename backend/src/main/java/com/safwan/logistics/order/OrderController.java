package com.safwan.logistics.order;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;

import java.net.URI;



@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping("/{id}")
public OrderResponse findById(@PathVariable("id") Long id) {
    return orderService.findById(id);
}

    @PostMapping
public ResponseEntity<OrderResponse> create(
        @Valid @RequestBody CreateOrderRequest request
) {
    OrderResponse response = orderService.create(request);

    URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();

    return ResponseEntity.created(location).body(response);
}

@PatchMapping("/{id}/status")
public OrderResponse updateStatus(
        @PathVariable("id") Long id,
        @Valid @RequestBody UpdateOrderStatusRequest request
) {
    return orderService.updateStatus(id, request);
}

  @GetMapping
public OrderPageResponse findAll(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(name = "status", required = false) OrderStatus status
) {
    return orderService.findAll(page, size, status);
}
}