package com.rubicon.water.repository;


import com.rubicon.water.entity.OrderScheduledInfo;
import org.springframework.data.repository.CrudRepository;

public interface IScheduledInfoRepo extends CrudRepository<OrderScheduledInfo, Long> {

	OrderScheduledInfo findByJobName(String jobName);
	
}
