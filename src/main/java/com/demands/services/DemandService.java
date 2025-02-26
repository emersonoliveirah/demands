package com.demands.services;

import com.demands.infraestructure.entity.DemandEntity;
import com.demands.infraestructure.entity.DemandStatus;
import com.demands.infraestructure.exceptions.DemandNotFound;
import com.demands.infraestructure.exceptions.InvalidStatusException;
import com.demands.infraestructure.repositories.DemandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandService {

    private final DemandRepository demandRepository;

    public List<DemandEntity> getDemandsByUserId(String userId) {
        List<DemandEntity> demands = demandRepository.findByUserId(userId);
        if (demands.isEmpty()) {
            throw new DemandNotFound("O usuário não possui demandas.");
        }
        return demands;
    }

    public List<DemandEntity> getAllDemands() {
        return demandRepository.findAll();
    }

    public List<DemandEntity> getDemandsByStatus(String status) {
        DemandStatus demandStatus;
        try {
            demandStatus = DemandStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Status incorreto: " + status);
        }
        List<DemandEntity> demands = demandRepository.findByStatus(demandStatus);
        if (demands.isEmpty()) {
            throw new DemandNotFound("Não existem demandas com o status informado.");
        }
        return demands;
    }

    public List<DemandEntity> getDemandsByAnyUserId(String userId) {
        return demandRepository.findByUserIdOrUserIdsContaining(userId, userId);
    }

    public DemandEntity createDemand(DemandEntity demand) {
        demand.setStartTime(LocalDateTime.now());
        demand.setTotalDuration(0);
        demand.setStatus(DemandStatus.OPEN);
        DemandEntity createdDemand = demandRepository.save(demand);

        if (demand.isAutoStart()) {
            startDemand(createdDemand.getDemandId());
        }

        return createdDemand;
    }

    public void startDemand(String demandId) {
        DemandEntity demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
        demand.setStartTime(LocalDateTime.now());
        demand.setPauseTime(null);
        demand.setStatus(DemandStatus.IN_PROGRESS);
        demandRepository.save(demand);
    }

    public void pauseDemand(String demandId) {
        DemandEntity demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
        if (demand.getStatus() == DemandStatus.CLOSED) {
            throw new IllegalStateException("Não é possível pausar uma demanda que já foi encerrada.");
        }
        demand.setPauseTime(LocalDateTime.now());
        long duration = Duration.between(demand.getStartTime(), demand.getPauseTime()).getSeconds();
        demand.setTotalDuration(demand.getTotalDuration() + duration);
        demand.setStatus(DemandStatus.PAUSED);
        demandRepository.save(demand);
    }

    public void continueDemand(String demandId) {
        DemandEntity demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
        if (demand.getStatus() == DemandStatus.CLOSED) {
            throw new IllegalStateException("Não é possível continuar uma demanda que já foi encerrada.");
        }
        demand.setStartTime(LocalDateTime.now());
        demand.setPauseTime(null);
        demand.setStatus(DemandStatus.IN_PROGRESS);
        demandRepository.save(demand);
    }

    public void closeDemand(String demandId) {
        DemandEntity demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
        if (demand.getPauseTime() == null) {
            demand.setPauseTime(LocalDateTime.now());
        }
        long duration = Duration.between(demand.getStartTime(), demand.getPauseTime()).getSeconds();
        demand.setTotalDuration(demand.getTotalDuration() + duration);
        demand.setStatus(DemandStatus.CLOSED);
        demandRepository.save(demand);
    }

    public void deleteDemand(String demandId) {
        DemandEntity demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
        demandRepository.delete(demand);
    }

    public DemandEntity getDemand(String demandId) {
        return demandRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
    }

    public void updateDemand(DemandEntity demand) {
        DemandEntity existingDemand = demandRepository.findById(demand.getDemandId())
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
        demandRepository.save(demand);
    }

    public void deleteAllDemands() {
        demandRepository.deleteAll();
    }

}