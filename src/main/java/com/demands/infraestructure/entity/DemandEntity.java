package com.demands.infraestructure.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "demand_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandEntity {

    @Id
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

    @LastModifiedDate
    private LocalDateTime statusDate;


    //ADD NEW FIELD FOR MULTIPLE USERS

    // GIT LAB PARA UPPAR O REPOSITÓRIO
    // DIAGRAMA DE DEMANDAS E USUÁRIOS
    // KEYCLOAK - AUTENTICAÇÃO - FUTURO - FRONTEND
    // KONG API MANAGER - GATEWAY - FUTURO - FRONTEND
    // DOCKER - CONTAINER - FUTURO - FRONTEND
    // KUBERNETES - ORQUESTRADOR - FUTURO - FRONTEND
    // QUARKUS - FRAMEWORK - FUTURO - FRONTEND
    // ARQUITETO DE SOLUÇÃO - PENSA EM TODA A APLICACAO

    // TECNOLIGIA PARA PAIR PROGAMMING
}