package com.demands.services;

import com.demands.infraestructure.entity.DemandEntity;
import com.demands.infraestructure.entity.DemandStatus;
import com.demands.infraestructure.exceptions.DemandNotFound;
import com.demands.infraestructure.exceptions.InvalidStatusException;
import com.demands.infraestructure.repositories.DemandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandService {

    private final DemandRepository demandRepository;

//    public List<DemandEntity> getDemandsByUserId(String userId) {
//        log.info("Fetching demands for userId: {}", userId);
//        List<DemandEntity> demands = demandRepository.findByUserId(userId);
//        if (demands.isEmpty()) {
//            log.warn("No demands found for userId: {}", userId);
//            throw new DemandNotFound("O usuário não possui demandas.");
//        }
//        return demands;
//    }
//
//    public List<DemandEntity> getAllDemands() {
//        log.info("Fetching all demands");
//        return demandRepository.findAll();
//    }

    public List<DemandEntity> getDemandsByUserEmail(String userEmail) {
        return demandRepository.findByUserId(userEmail);
    }

    public List<DemandEntity> getDemandsByStatus(String status) {
        log.info("Fetching demands with status: {}", status);
        DemandStatus demandStatus;
        try {
            demandStatus = DemandStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid status: {}", status);
            throw new InvalidStatusException("Status incorreto: " + status);
        }
        List<DemandEntity> demands = demandRepository.findByStatus(demandStatus);
        if (demands.isEmpty()) {
            log.warn("No demands found with status: {}", status);
            throw new DemandNotFound("Não existem demandas com o status informado.");
        }
        return demands;
    }

    public List<DemandEntity> getDemandsByAnyUserId(String userId) {
        log.info("Fetching demands for any userId: {}", userId);
        return demandRepository.findByUserIdOrUserIdsContaining(userId, userId);
    }

    public DemandEntity createDemand(DemandEntity demand) {
        log.info("Creating new demand: {}", demand);
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
        log.info("Starting demand with id: {}", demandId);
        DemandEntity demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
        demand.setStartTime(LocalDateTime.now());
        demand.setPauseTime(null);
        demand.setStatus(DemandStatus.IN_PROGRESS);
        demandRepository.save(demand);
    }

    public void pauseDemand(String demandId) {
        log.info("Pausing demand with id: {}", demandId);
        DemandEntity demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
        if (demand.getStatus() == DemandStatus.CLOSED) {
            log.error("Cannot pause a closed demand with id: {}", demandId);
            throw new IllegalStateException("Não é possível pausar uma demanda que já foi encerrada.");
        }
        demand.setPauseTime(LocalDateTime.now());
        long duration = Duration.between(demand.getStartTime(), demand.getPauseTime()).getSeconds();
        demand.setTotalDuration(demand.getTotalDuration() + duration);
        demand.setStatus(DemandStatus.PAUSED);
        demandRepository.save(demand);
    }

    public void continueDemand(String demandId) {
        log.info("Continuing demand with id: {}", demandId);
        DemandEntity demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
        if (demand.getStatus() == DemandStatus.CLOSED) {
            log.error("Cannot continue a closed demand with id: {}", demandId);
            throw new IllegalStateException("Não é possível continuar uma demanda que já foi encerrada.");
        }
        demand.setStartTime(LocalDateTime.now());
        demand.setPauseTime(null);
        demand.setStatus(DemandStatus.IN_PROGRESS);
        demandRepository.save(demand);
    }

    public void closeDemand(String demandId) {
        log.info("Closing demand with id: {}", demandId);
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
        log.info("Deleting demand with id: {}", demandId);
        DemandEntity demand = demandRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
        demandRepository.delete(demand);
    }

    public DemandEntity getDemand(String demandId) {
        log.info("Fetching demand with id: {}", demandId);
        return demandRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
    }

    public void updateDemand(DemandEntity demand) {
        log.info("Updating demand: {}", demand);
        DemandEntity existingDemand = demandRepository.findById(demand.getDemandId())
                .orElseThrow(() -> new DemandNotFound("Demanda não encontrada"));
        demandRepository.save(demand);
    }

    public void deleteAllDemands() {
        log.info("Deleting all demands");
        demandRepository.deleteAll();
    }

    public void updateDemandTimer(String demandId, String startTime, String endTime) {
        DemandEntity demand = getDemand(demandId);
        if (demand == null) {
            throw new DemandNotFound("Demanda não encontrada.");
        }

        // Convert String to LocalDateTime
        demand.setStartTime(LocalDateTime.parse(startTime));
        demand.setEndTime(LocalDateTime.parse(endTime));

        // Calculate the total duration
        long duration = Duration.between(
                LocalDateTime.parse(startTime),
                LocalDateTime.parse(endTime)
        ).toSeconds();
        demand.setTotalDuration(duration);

        demandRepository.save(demand);
    }
}
