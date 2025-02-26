package com.demands.dtos;

import com.demands.infraestructure.entity.DemandStatus;
import com.demands.infraestructure.entity.DemandType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String demandId;
    private String userId; // Usuário principal
    private List<String> userIds; // Lista de IDs de usuários adicionais
    private String title;
    private String description;
    private DemandStatus status;
    private String startDate;
    private String endDate;
    private DemandType type;
    private LocalDateTime startTime;
    private LocalDateTime pauseTime;
    private long totalDuration; // in seconds
    private boolean autoStart;
    private LocalDateTime statusDate;
}