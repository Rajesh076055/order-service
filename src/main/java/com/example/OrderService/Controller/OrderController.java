package com.example.OrderService.Controller;
import java.util.List;
import com.example.OrderService.DTO.OrderRequest;
import com.example.OrderService.DTO.OrderResponse;
import com.example.OrderService.OrderServiceApplication;
import com.example.OrderService.Service.OrderService;
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
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            this.orderService.placeOrder(orderRequest);
            return new ResponseEntity<>("Order Created Successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Item not in the stock", HttpStatus.UNPROCESSABLE_ENTITY);
        }

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
