package com.rubicon.water.repository;

import com.rubicon.water.Enums.OrderStatus;
import com.rubicon.water.entity.WaterOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IWaterOrderRepo extends JpaRepository<WaterOrder, Long> {

    List<WaterOrder> findByFarmId(String farmId);

    @Modifying
    @Query("update WaterOrder wo set wo.status = :status, wo.updatedAt = :updateTime where wo.orderId = :orderId")
    void setOrderStatusByOrderId(OrderStatus status, Long orderId, LocalDateTime updateTime);
}
