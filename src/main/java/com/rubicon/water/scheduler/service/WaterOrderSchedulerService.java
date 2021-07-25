package com.rubicon.water.scheduler.service;


import com.rubicon.water.Enums.JobType;
import com.rubicon.water.constants.OrderConstants;
import com.rubicon.water.entity.OrderScheduledInfo;
import com.rubicon.water.repository.IScheduledInfoRepo;
import com.rubicon.water.scheduler.listener.WaterOrderSchedulerListener;
import com.rubicon.water.scheduler.job.WaterOrderStartJob;
import com.rubicon.water.scheduler.job.WaterOrderStopJob;
import com.rubicon.water.scheduler.jobdata.JobData;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Component
@Transactional
public class WaterOrderSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaterOrderSchedulerService.class);

    Scheduler quartzScheduler;

    WaterOrderSchedulerListener waterOrderSchedulerListener;

    IScheduledInfoRepo scheduledInfoRepo;


    @Autowired
    public WaterOrderSchedulerService(Scheduler quartzScheduler, WaterOrderSchedulerListener waterOrderSchedulerListener, IScheduledInfoRepo scheduledInfoRepo) {
        this.quartzScheduler = quartzScheduler;
        this.waterOrderSchedulerListener = waterOrderSchedulerListener;
        this.scheduledInfoRepo = scheduledInfoRepo;
    }

    @PostConstruct
    public void postConstruct() {
        try {
            quartzScheduler.start();
            quartzScheduler.getListenerManager().addSchedulerListener(waterOrderSchedulerListener);
        } catch (SchedulerException exception) {
            LOGGER.error("scheduler throws exception " + exception);
        }
    }

    @PreDestroy
    public void preDestroy() {
        try {
            quartzScheduler.shutdown();
        } catch (SchedulerException exception) {
            LOGGER.error("scheduler throws exception " + exception);
        }
    }

    public void schedule(JobData data) throws SchedulerException {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(OrderConstants.FARM, data.getFarmId());
        dataMap.put(OrderConstants.ORDER_ID, data.getOrderId());

        scheduleStartJob(data, dataMap);

        scheduleStopJob(data, dataMap);

        LOGGER.info("Water order {} Scheduled from {} to {}", data.getOrderId(), data.getStartTime(), data.getStartTime().plusMinutes(data.getDurationInHours()));
    }

    private void scheduleStartJob(JobData data, JobDataMap dataMap) throws SchedulerException {
        dataMap.put(OrderConstants.JOB_TYPE, JobType.START);
        ZonedDateTime startZonedDateTime = ZonedDateTime.of(data.getStartTime(), ZoneId.of("Asia/Kolkata"));
        scheduleJob(data, data.getJobName().concat(JobType.START.name()), data.getJobGroup(), startZonedDateTime, dataMap, WaterOrderStartJob.class);
    }

    private void scheduleStopJob(JobData data, JobDataMap dataMap) throws SchedulerException {
        dataMap.put(OrderConstants.JOB_TYPE, JobType.STOP);
        ZonedDateTime stopZonedDateTime = ZonedDateTime.of(data.getStartTime().plusMinutes(data.getDurationInHours()), ZoneId.of("Asia/Kolkata"));
        scheduleJob(data, data.getJobName().concat(JobType.STOP.name()), data.getJobGroup(), stopZonedDateTime, dataMap, WaterOrderStopJob.class);
    }


    private void scheduleJob(JobData data, String jobName, String jobGroup, ZonedDateTime zonedDateTime, JobDataMap dataMap, Class<? extends QuartzJobBean> jobClass) throws SchedulerException {
        JobDetail detail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup).usingJobData(dataMap).storeDurably(false)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup)
                .startAt(Date.from(zonedDateTime.toInstant())).usingJobData(dataMap)
                .build();
        try {
            quartzScheduler.scheduleJob(detail, trigger);
            OrderScheduledInfo orderScheduledInfo = OrderScheduledInfo.builder().jobGroup(jobGroup).jobName(jobName).startTime(zonedDateTime.toLocalDateTime()).farmId(data.getFarmId())
                    .orderId(data.getOrderId()).build();
            scheduledInfoRepo.save(orderScheduledInfo);
        } catch (SchedulerException e) {
            LOGGER.error("Error occurred while scheduling a job " + e);
            throw e;
        }
    }

    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        try {
            quartzScheduler.deleteJob(jobKey);
            OrderScheduledInfo orderScheduledInfo = scheduledInfoRepo.findByJobName(jobName);
            if(orderScheduledInfo !=null)
                scheduledInfoRepo.delete(orderScheduledInfo);

        } catch (SchedulerException e) {
            LOGGER.error("Error occurred while deleting the scheduled job " + e);
            throw e;
        }
    }

    public void deleteJob(List<JobKey> jobKeyList) throws SchedulerException {
        for (JobKey jobKey : jobKeyList) {
            try {
                quartzScheduler.deleteJob(jobKey);
                OrderScheduledInfo orderScheduledInfo = scheduledInfoRepo.findByJobName(jobKey.getName());
                if (orderScheduledInfo != null)
                    scheduledInfoRepo.delete(orderScheduledInfo);

            } catch (SchedulerException e) {
                LOGGER.error("Error occurred while deleting the scheduled job " + e);
                throw e;
            }
        }
    }


}
