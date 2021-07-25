package com.rubicon.water.service;

import com.rubicon.water.entity.WaterOrder;
import com.rubicon.water.exception.OrderException;
import com.rubicon.water.request.WaterOrderRequest;
import com.rubicon.water.response.OrderResponse;

import java.util.List;
import java.util.Optional;

public interface IWaterOrderService {

    OrderResponse cancelOrder(Long id) throws OrderException;
    OrderResponse createOrder(WaterOrderRequest waterOrderRequest) throws OrderException;
    List<WaterOrder> findAll();
    Optional<WaterOrder> findById(Long id);
}
