package com.rubicon.water.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@Builder
public class WaterOrderRequest {
    @NotNull(message = "Please provide a farmId")
    @NotBlank
    private String farmId;
    @NotNull(message = "Please provide a startDateTime")
    private LocalDateTime startDateTime;
    @NotNull(message = "Please provide the duration time")
    private Long durationInHours;
}
