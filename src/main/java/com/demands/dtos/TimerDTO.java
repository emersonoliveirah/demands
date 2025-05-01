package com.demands.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimerDTO {
    private String startTime;
    private String endTime;
}
