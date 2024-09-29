package com.example.OrderService.Controller;
import java.util.List;
import com.example.OrderService.DTO.OrderRequest;
import com.example.OrderService.OrderServiceApplication;
import com.example.OrderService.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody OrderRequest orderRequest) {
        this.orderService.placeOrder(orderRequest);
        return "Order Created Successfully";
    }

//    @DeleteMapping
//    @ResponseStatus(HttpStatus.OK)
//    public void deleteAllOrder() {
//        this.orderService.deleteAllOrder();
//    }
}
