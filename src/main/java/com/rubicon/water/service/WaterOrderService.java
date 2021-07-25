package com.rubicon.water.service;

import com.rubicon.water.Enums.JobType;
import com.rubicon.water.Enums.OrderStatus;
import com.rubicon.water.Enums.ResponseCode;
import com.rubicon.water.exception.OrderException;
import com.rubicon.water.response.OrderResponse;
import com.rubicon.water.controller.WaterOrderController;
import com.rubicon.water.entity.WaterOrder;
import com.rubicon.water.repository.IWaterOrderRepo;
import com.rubicon.water.request.WaterOrderRequest;
import com.rubicon.water.scheduler.jobdata.JobData;
import com.rubicon.water.scheduler.service.WaterOrderSchedulerService;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class WaterOrderService implements IWaterOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaterOrderController.class);

    private IWaterOrderRepo waterOrderRepo;

    private WaterOrderSchedulerService schedulerService;

    @Autowired
    public WaterOrderService(IWaterOrderRepo waterOrderRepo, WaterOrderSchedulerService schedulerService) {
        this.waterOrderRepo = waterOrderRepo;
        this.schedulerService = schedulerService;
    }

    @Override
    public OrderResponse cancelOrder(Long id) throws OrderException {
        Optional<WaterOrder> waterOrder = waterOrderRepo.findById(id);
        if (waterOrder.isPresent() && waterOrder.get().getStatus() != OrderStatus.DELIVERED) {
            WaterOrder updateWaterOrder = waterOrder.get();
            updateWaterOrder.setStatus(OrderStatus.CANCELLED);
            waterOrderRepo.save(updateWaterOrder);

            deleteScheduledJobs(updateWaterOrder);

            LOGGER.info("Water order is Cancelled: {}", updateWaterOrder);

            return new OrderResponse("Order cancelled", ResponseCode.OK, updateWaterOrder);
        } else {
            return new OrderResponse("Order is already Delivered", ResponseCode.ERROR, null);
        }
    }

    private void deleteScheduledJobs(WaterOrder updateWaterOrder) throws OrderException {
        List<JobKey> jobKeys = new ArrayList<>();
        final String jobName = updateWaterOrder.getFarmId().concat(String.valueOf(updateWaterOrder.getOrderId()));
        String jobGroup = updateWaterOrder.getFarmId();
        jobKeys.add(new JobKey(jobName.concat(JobType.START.name()), jobGroup));
        jobKeys.add(new JobKey(jobName.concat(JobType.STOP.name()), jobGroup));
        try {
            schedulerService.deleteJob(jobKeys);
        } catch (SchedulerException e) {
            LOGGER.error("Error occurred while deleting the scheduled job " + e);
            throw new OrderException("Error occurred while deleting the scheduled job");
        }
    }

    @Override
    public OrderResponse createOrder(WaterOrderRequest waterOrderRequest) throws OrderException {
        OrderResponse orderResponse;
        List<WaterOrder> filteredList = new ArrayList<>();

        List<WaterOrder> listOfWaterOrders = waterOrderRepo.findByFarmId(waterOrderRequest.getFarmId());

        if (!listOfWaterOrders.isEmpty()) {
            filteredList = listOfWaterOrders.stream().filter(wo -> waterOrderRequest.getStartDateTime().isAfter(wo.getStartDateTime()) &&
                    waterOrderRequest.getStartDateTime().isBefore(wo.getStartDateTime().plusMinutes(wo.getDurationInHours())) ||
                    waterOrderRequest.getStartDateTime().equals(wo.getStartDateTime())).collect(Collectors.toList());
        }

        if (!filteredList.isEmpty()) {
            LOGGER.info("Order for the Requested Time already Exist.");
            orderResponse = new OrderResponse("Order for the Requested Time already Exist", ResponseCode.ERROR, null);
        }
        else {
            WaterOrder waterOrder = WaterOrder.builder().farmId(waterOrderRequest.getFarmId()).startDateTime(waterOrderRequest.getStartDateTime())
                    .durationInHours(waterOrderRequest.getDurationInHours()).status(OrderStatus.REQUESTED).build();
            final WaterOrder newWaterOrder = waterOrderRepo.save(waterOrder);

            scheduleWaterOrder(newWaterOrder);

            LOGGER.info("New water order is created: {}", newWaterOrder);
            orderResponse = new OrderResponse("Order Created", ResponseCode.OK, newWaterOrder);
        }
        return orderResponse;
    }

    private void scheduleWaterOrder(WaterOrder waterOrder) throws OrderException {
        String jobName = waterOrder.getFarmId().concat(String.valueOf(waterOrder.getOrderId()));
        JobData jobData = JobData.builder().jobName(jobName).jobGroup(waterOrder.getFarmId()).durationInHours(waterOrder.getDurationInHours())
                .startTime(waterOrder.getStartDateTime()).farmId(waterOrder.getFarmId()).orderId(waterOrder.getOrderId()).build();
        try {
            schedulerService.schedule(jobData);
        } catch (SchedulerException e) {
            LOGGER.error("Error occurred while scheduling the job " + e);
            throw new OrderException("Error occurred while scheduling the job");
        }
    }

    @Override
    public List<WaterOrder> findAll() {
        return waterOrderRepo.findAll();
    }

    @Override
    public Optional<WaterOrder> findById(Long id) {
        return waterOrderRepo.findById(id);
    }
}
