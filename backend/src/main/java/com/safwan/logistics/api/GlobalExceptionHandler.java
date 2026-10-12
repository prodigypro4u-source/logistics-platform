package com.safwan.logistics.api;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.safwan.logistics.order.OrderNotFoundException;
import com.safwan.logistics.shipment.ShipmentConflictException;
import com.safwan.logistics.order.InvalidOrderQueryException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.safwan.logistics.order.InvalidOrderStatusException;
import com.safwan.logistics.shipment.ShipmentNotFoundException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.putIfAbsent(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ApiError response = new ApiError(
                Instant.now(),
                400,
                "Validation failed",
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }
@ExceptionHandler(InvalidOrderStatusException.class)
public ResponseEntity<ApiError> handleInvalidOrderStatus(
        InvalidOrderStatusException exception
) {
    return ResponseEntity.status(409).body(
            new ApiError(
                    Instant.now(),
                    409,
                    exception.getMessage(),
                    Map.of()
            )
    );
}

@ExceptionHandler(ShipmentNotFoundException.class)
public ResponseEntity<ApiError> handleShipmentNotFound(
        ShipmentNotFoundException exception
) {
    return ResponseEntity.status(404).body(
            new ApiError(
                    Instant.now(),
                    404,
                    exception.getMessage(),
                    Map.of()
            )
    );
}



@ExceptionHandler(OrderNotFoundException.class)
public ResponseEntity<ApiError> handleOrderNotFound(
        OrderNotFoundException exception
) {
    ApiError response = new ApiError(
            Instant.now(),
            404,
            exception.getMessage(),
            Map.of()
    );

    return ResponseEntity.status(404).body(response);

}
@ExceptionHandler(InvalidOrderQueryException.class)
public ResponseEntity<ApiError> handleInvalidOrderQuery(
        InvalidOrderQueryException exception
) {
    return ResponseEntity.badRequest().body(
            new ApiError(
                    Instant.now(),
                    400,
                    exception.getMessage(),
                    Map.of()
            )
    );
}

@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ResponseEntity<ApiError> handleTypeMismatch(
        MethodArgumentTypeMismatchException exception
) {
    return ResponseEntity.badRequest().body(
            new ApiError(
                    Instant.now(),
                    400,
                    "Invalid value for parameter: " + exception.getName(),
                    Map.of()
            )
    );
}

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(
            HttpMessageNotReadableException exception
    ) {
        ApiError response = new ApiError(
                Instant.now(),
                400,
                "Request body is missing or contains invalid JSON",
                Map.of()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ShipmentConflictException.class)
public ResponseEntity<ApiError> handleShipmentConflict(
        ShipmentConflictException exception
) {
    return ResponseEntity.status(409).body(
            new ApiError(
                    Instant.now(),
                    409,
                    exception.getMessage(),
                    Map.of()
            )
    );
}

    public record ApiError(
            Instant timestamp,
            int status,
            String message,
            Map<String, String> fieldErrors
    ) {}





    

}