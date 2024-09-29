package com.example.OrderService.Service;


import com.example.OrderService.DTO.OrderLineItemsDTO;
import com.example.OrderService.DTO.OrderRequest;
import com.example.OrderService.Model.Order;
import com.example.OrderService.Model.OrderListItem;
import com.example.OrderService.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderListItem> orderListItem = orderRequest.getOrderLineItemsDTO()
                .stream()
                .map(this::mapToDTO)
                .toList();

//        Method to check if the item is in the inventory
        order.setOrderListItems(orderListItem);
        this.orderRepository.save(order);
    }

    public void deleteAllOrder() {
        this.orderRepository.deleteAll();
    }

    private OrderListItem mapToDTO(OrderLineItemsDTO orderLineItemsDTO) {
        OrderListItem orderListItem = new OrderListItem();
        orderListItem.setQuantity(orderLineItemsDTO.getQuantity());
        orderListItem.setPrice(orderLineItemsDTO.getPrice());
        orderListItem.setSkuCode(orderLineItemsDTO.getSkuCode());

        return orderListItem;
    }
}
