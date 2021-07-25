package com.rubicon.water.scheduler.jobdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobData {

	private String jobName;
	private String jobGroup;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;
	private Long  durationInHours;
	private String farmId;
	private Long orderId;
	
}
