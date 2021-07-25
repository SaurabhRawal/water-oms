
package com.rubicon.water.scheduler.job;

import com.rubicon.water.constants.OrderConstants;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;


@Component
public class WaterOrderStopJob extends QuartzJobBean {

    private static final Logger logger = LoggerFactory.getLogger(WaterOrderStopJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        logger.info("Water delivery to Farm {} stopped.", mergedJobDataMap.get(OrderConstants.FARM));
    }

}
