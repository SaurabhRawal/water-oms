package com.rubicon.water.scheduler.job;

import com.rubicon.water.constants.OrderConstants;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


@Component
public class WaterOrderStartJob extends QuartzJobBean {

	private static final Logger logger = LoggerFactory.getLogger(WaterOrderStartJob.class);

	@Override
	protected void executeInternal(JobExecutionContext context) {
		JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
		logger.info("Water delivery to Farm {} started.", mergedJobDataMap.get(OrderConstants.FARM));
	}

}
