package com.safwan.logistics.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank(message = "Customer name is required")
        @Size(
                max = 120,
                message = "Customer name must be 120 characters or fewer"
        )
        String customerName,

        @NotBlank(message = "Delivery address is required")
        @Size(
                max = 500,
                message = "Delivery address must be 500 characters or fewer"
        )
        String deliveryAddress
) {}