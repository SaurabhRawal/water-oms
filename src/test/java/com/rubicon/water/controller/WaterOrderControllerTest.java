package com.rubicon.water.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rubicon.water.entity.WaterOrder;
import com.rubicon.water.request.WaterOrderRequest;
import com.rubicon.water.response.OrderResponse;
import com.rubicon.water.service.IWaterOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WaterOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean()
    private IWaterOrderService waterOrderService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getAllOrderTest() throws Exception {
        mockMvc.perform(get("/api/water-order")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    public void getOrderTest() throws Exception {
        when(waterOrderService.findById(1L)).thenReturn(Optional.of(WaterOrder.builder().orderId(1L).build()));
        mockMvc.perform(get("/api/water-order/1")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        verify(waterOrderService, times(1)).findById(1L);
    }


    @Test
    public void createOrderTest() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now();

        WaterOrderRequest waterOrderRequest = WaterOrderRequest.builder().startDateTime(localDateTime.plusHours(5)).farmId("123").durationInHours(1L).build();
        objectMapper.registerModule(new JavaTimeModule());
        String valueAsString = objectMapper.writeValueAsString(waterOrderRequest);

        mockMvc.perform(post("/api/water-order")
                .content(valueAsString)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    public void cancelOrder() throws Exception {
        when(waterOrderService.cancelOrder(1L)).thenReturn(OrderResponse.builder().body(WaterOrder.builder().orderId(1L).build()).build());
        mockMvc.perform(put("/api/water-order/1")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        verify(waterOrderService, times(1)).cancelOrder(1L);

    }
}
