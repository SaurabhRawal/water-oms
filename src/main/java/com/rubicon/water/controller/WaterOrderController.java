package com.rubicon.water.controller;


import com.rubicon.water.Enums.ResponseCode;
import com.rubicon.water.entity.WaterOrder;
import com.rubicon.water.request.WaterOrderRequest;
import com.rubicon.water.response.OrderResponse;
import com.rubicon.water.service.IWaterOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class WaterOrderController {


    private IWaterOrderService waterOrderService;

    @Autowired
    public WaterOrderController(IWaterOrderService waterOrderService){
        this.waterOrderService = waterOrderService;
    }

    @GetMapping("/water-order")
    public ResponseEntity<OrderResponse> getAllWaterOrders() {
        try {
            List<WaterOrder> list = waterOrderService.findAll();
            if (list.isEmpty() || list.size() == 0) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            }
            OrderResponse response = OrderResponse.builder().responseCode(ResponseCode.OK).body(list).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/water-order/{id}")
    public ResponseEntity<OrderResponse> getWaterOrder(@PathVariable Long id) {
        try {
            Optional<WaterOrder> waterOrder = waterOrderService.findById(id);
            if (waterOrder.isPresent()) {
                OrderResponse response = OrderResponse.builder().responseCode(ResponseCode.OK).body(waterOrder.get()).build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/water-order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody WaterOrderRequest waterOrderRequest) {
        Assert.isTrue(waterOrderRequest.getStartDateTime().isAfter(LocalDateTime.now()), "Please provide Time greater than now");
        try {
            OrderResponse orderResponse = waterOrderService.createOrder(waterOrderRequest);
            if (orderResponse.getResponseCode() == ResponseCode.OK) {
                return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(orderResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/water-order/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            OrderResponse orderResponse = waterOrderService.cancelOrder(id);
            return new ResponseEntity<>(orderResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
