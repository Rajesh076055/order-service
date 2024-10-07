package com.example.OrderService;

import com.example.OrderService.DTO.OrderLineItemsDTO;
import com.example.OrderService.DTO.OrderRequest;
import com.example.OrderService.Model.Order;
import com.example.OrderService.Model.OrderListItem;
import com.example.OrderService.Repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgreSQLContainer =
			new PostgreSQLContainer<>("postgres:17.0")
					.withUsername("test")
					.withPassword("test")
					.withDatabaseName("test");
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private OrderRepository orderRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	}

	@Test
	void testCreateOrderSuccess() throws Exception {
		OrderRequest orderRequest = getOrderRequest();
		String orderRequestString = objectMapper.writeValueAsString(orderRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
				.contentType(MediaType.APPLICATION_JSON)
				.content(orderRequestString))
				.andExpect(status().isCreated());

		List<String> skuCodes = this.orderRepository.findAll()
				.stream()
				.flatMap(eachOrder -> eachOrder.getOrderListItems().stream()
						.map(OrderListItem::getSkuCode))
				.toList();

		Assertions.assertEquals("iPhone 12", skuCodes.get(0));
		Assertions.assertEquals("iPhone 13", skuCodes.get(1));
	}

	@Test
	void testCreateOrderFail() throws Exception {
		OrderRequest orderRequest = getOrderRequestFail();
		String orderRequestString = objectMapper.writeValueAsString(orderRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content(orderRequestString))
						.andExpect(status().isUnprocessableEntity());

	}

	private OrderRequest getOrderRequest() {
		return OrderRequest.builder().orderLineItemsDTO(List.of(
				OrderLineItemsDTO.builder()
						.price(BigDecimal.valueOf(1200))
						.skuCode("iPhone 12")
						.quantity(12)
						.build(),
				OrderLineItemsDTO.builder()
						.price(BigDecimal.valueOf(1300))
						.skuCode("iPhone 13")
						.quantity(13)
						.build()
				)).build();
	}

	private OrderRequest getOrderRequestFail() {
		return OrderRequest.builder().orderLineItemsDTO(List.of(
				OrderLineItemsDTO.builder()
						.price(BigDecimal.valueOf(1200))
						.skuCode("iPhone 12")
						.quantity(0)
						.build(),
				OrderLineItemsDTO.builder()
						.price(BigDecimal.valueOf(1300))
						.skuCode("iPhone 13")
						.quantity(0)
						.build()
		)).build();
	}
}
