package com.example.OrderService.Controller;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.OrderService.DTO.OrderRequest;
import com.example.OrderService.DTO.OrderResponse;
import com.example.OrderService.OrderServiceApplication;
import com.example.OrderService.Service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> createOrder(@RequestBody OrderRequest orderRequest) {
       return CompletableFuture.supplyAsync(() -> this.orderService.placeOrder(orderRequest));
    }

    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> "Something went wrong! Please order after sometime.");
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrder(@PathVariable Long id) {
        return this.orderService.getOrder(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllOrder() {
        this.orderService.deleteAllOrder();
    }
}
