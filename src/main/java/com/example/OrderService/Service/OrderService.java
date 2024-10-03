package com.example.OrderService.Service;


import com.example.OrderService.DTO.OrderLineItemsDTO;
import com.example.OrderService.DTO.OrderRequest;
import com.example.OrderService.DTO.OrderResponse;
import com.example.OrderService.Model.Order;
import com.example.OrderService.Model.OrderListItem;
import com.example.OrderService.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;
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
                .map(this::mapFromDTO)
                .toList();

//        Method to check if the item is in the inventory
        order.setOrderListItems(orderListItem);
        this.orderRepository.save(order);
    }


    public OrderResponse getOrder(Long id) {
            Order order = this.orderRepository.findById(String.valueOf(id)).orElse(null);
            OrderResponse response = new OrderResponse();

            List<OrderLineItemsDTO> orderLineItemsDTO = order
                    .getOrderListItems()
                    .stream()
                    .map(this::mapToDTO)
                    .toList();

            response.setOrderLineItemsDTO(orderLineItemsDTO);
            return response;

    }


    public void deleteAllOrder() {
        this.orderRepository.deleteAll();
    }

    private OrderListItem mapFromDTO(OrderLineItemsDTO orderLineItemsDTO) {
        OrderListItem orderListItem = new OrderListItem();
        orderListItem.setQuantity(orderLineItemsDTO.getQuantity());
        orderListItem.setPrice(orderLineItemsDTO.getPrice());
        orderListItem.setSkuCode(orderLineItemsDTO.getSkuCode());

        return orderListItem;
    }

    private OrderLineItemsDTO mapToDTO(OrderListItem orderListItem) {
        OrderLineItemsDTO orderLineItemsDTO = new OrderLineItemsDTO();
        orderLineItemsDTO.setPrice(orderListItem.getPrice());
        orderLineItemsDTO.setQuantity(orderListItem.getQuantity());
        orderLineItemsDTO.setSkuCode(orderListItem.getSkuCode());

        return orderLineItemsDTO;
    }

}
