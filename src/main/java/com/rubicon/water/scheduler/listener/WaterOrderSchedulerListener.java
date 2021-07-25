package com.rubicon.water.scheduler.listener;

import com.rubicon.water.Enums.JobType;
import com.rubicon.water.Enums.OrderStatus;
import com.rubicon.water.constants.OrderConstants;
import com.rubicon.water.entity.OrderScheduledInfo;
import com.rubicon.water.repository.IScheduledInfoRepo;
import com.rubicon.water.repository.IWaterOrderRepo;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
public class WaterOrderSchedulerListener implements SchedulerListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(WaterOrderSchedulerListener.class);

	private IScheduledInfoRepo scheduledInfoRepo;

	private IWaterOrderRepo waterOrderRepo;

	@Autowired
	public WaterOrderSchedulerListener(IScheduledInfoRepo scheduledInfoRepo, IWaterOrderRepo waterOrderRepo) {
		this.scheduledInfoRepo = scheduledInfoRepo;
		this.waterOrderRepo = waterOrderRepo;
	}

	@Override
	@Transactional
	public void triggerFinalized(Trigger trigger) {
		try {
			JobKey jobKey = trigger.getJobKey();
			String jobName = jobKey.getName();

			OrderScheduledInfo persistedOrderScheduledInfo = scheduledInfoRepo.findByJobName(jobName);
			if (persistedOrderScheduledInfo != null)
				scheduledInfoRepo.delete(persistedOrderScheduledInfo);

			if (trigger.getJobDataMap().get(OrderConstants.JOB_TYPE).equals(JobType.START)) {
				waterOrderRepo.setOrderStatusByOrderId(OrderStatus.IN_PROGRESS, (Long) trigger.getJobDataMap().get(OrderConstants.ORDER_ID), LocalDateTime.now());
				LOGGER.info("Order status for order {} changed to IN_PROGRESS", trigger.getJobDataMap().get(OrderConstants.ORDER_ID));
			} else if (trigger.getJobDataMap().get(OrderConstants.JOB_TYPE).equals(JobType.STOP)) {
				waterOrderRepo.setOrderStatusByOrderId(OrderStatus.DELIVERED, (Long) trigger.getJobDataMap().get(OrderConstants.ORDER_ID), LocalDateTime.now());
				LOGGER.info("Order status for order {} changed to DELIVERED", trigger.getJobDataMap().get(OrderConstants.ORDER_ID));
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while job clean up");
		}
	}

	@Override
	public void jobScheduled(Trigger trigger) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobUnscheduled(TriggerKey triggerKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void triggerPaused(TriggerKey triggerKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void triggersPaused(String triggerGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void triggerResumed(TriggerKey triggerKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void triggersResumed(String triggerGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobAdded(JobDetail jobDetail) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobDeleted(JobKey jobKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobPaused(JobKey jobKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobsPaused(String jobGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobResumed(JobKey jobKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobsResumed(String jobGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerInStandbyMode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerStarting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerShutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerShuttingdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulingDataCleared() {
		// TODO Auto-generated method stub
		
	}

}
