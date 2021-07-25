package com.rubicon.water.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rubicon.water.Enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name="water_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderId;
    @Column(name = "farmId")
    private String farmId;
    @Column(name = "start_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime ;
    @Column(name = "duration_in_hour")
    private Long  durationInHours;
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_created")
    private LocalDateTime createdAt;
    @Column(name = "date_updated")
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

}
