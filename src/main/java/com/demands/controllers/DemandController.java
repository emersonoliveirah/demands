package com.demands.controllers;

import com.demands.dtos.DemandDTO;
import com.demands.dtos.TimerDTO;
import com.demands.infraestructure.entity.DemandEntity;
import com.demands.infraestructure.exceptions.ApiResponse;
import com.demands.infraestructure.exceptions.DemandNotFound;
import com.demands.infraestructure.exceptions.InvalidStatusException;
import com.demands.security.JwtUtil;
import com.demands.services.DemandService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/demands")
@RequiredArgsConstructor
public class DemandController {

    private final DemandService demandService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse> createDemand(HttpServletRequest request, @Valid @RequestBody DemandDTO demandDTO) {
        String userId = (String) request.getAttribute("userId");
        demandDTO.setUserId(userId); // <-- garanta que salva o email do usuário autenticado
        DemandEntity demand = convertToEntity(demandDTO);
        DemandEntity createdDemand = demandService.createDemand(demand);
        DemandDTO createdDemandDTO = convertToDTO(createdDemand);
        ApiResponse response = new ApiResponse(HttpStatus.OK.value(), "Demanda criada com sucesso.", createdDemandDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{demandId}/start")
    public ResponseEntity<ApiResponse> startDemand(@PathVariable String demandId) {
        demandService.startDemand(demandId);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK.value(), "Demanda iniciada com sucesso."), HttpStatus.OK);
    }

    @PutMapping("/{demandId}/pause")
    public ResponseEntity<ApiResponse> pauseDemand(@PathVariable String demandId) {
        try {
            demandService.pauseDemand(demandId);
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK.value(), "Demanda pausada com sucesso."), HttpStatus.OK);
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (DemandNotFound ex) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{demandId}/continue")
    public ResponseEntity<ApiResponse> continueDemand(@PathVariable String demandId) {
        try {
            demandService.continueDemand(demandId);
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK.value(), "Demanda continuada com sucesso."), HttpStatus.OK);
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (DemandNotFound ex) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{demandId}/close")
    public ResponseEntity<ApiResponse> closeDemand(@PathVariable String demandId) {
        demandService.closeDemand(demandId);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK.value(), "Demanda encerrada com sucesso."), HttpStatus.OK);
    }

    @DeleteMapping("/{demandId}")
    public ResponseEntity<ApiResponse> deleteDemand(@PathVariable String demandId) {
        demandService.deleteDemand(demandId);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK.value(), "Demanda deletada com sucesso."), HttpStatus.OK);
    }
//
//    @GetMapping("/{demandId}")
//    public ResponseEntity<DemandDTO> getDemand(@PathVariable String demandId) {
//        DemandEntity demand = demandService.getDemand(demandId);
//        DemandDTO demandDTO = convertToDTO(demand);
//        return ResponseEntity.ok(demandDTO);
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<?> getDemandsByAnyUserId(@PathVariable String userId) {
//        try {
//            List<DemandEntity> demands = demandService.getDemandsByAnyUserId(userId);
//            List<DemandDTO> demandDTOs = demands.stream()
//                    .map(this::convertToDTO)
//                    .collect(Collectors.toList());
//            return ResponseEntity.ok(demandDTOs);
//        } catch (DemandNotFound ex) {
//            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
//        }
//    }

    @GetMapping
    public ResponseEntity<List<DemandDTO>> getUserDemands(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId"); // Use userId instead of userEmail
        List<DemandEntity> demands = demandService.getDemandsByUserId(userId); // Update service call
        List<DemandDTO> demandDTOs = demands.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(demandDTOs);
    }

    @PutMapping("/{demandId}/update")
    public ResponseEntity<ApiResponse> updateDemand(@PathVariable String demandId, @RequestBody DemandDTO demandDTO) {
        // Buscar a demanda existente para preservar o userId
        DemandEntity existingDemand = demandService.getDemand(demandId);
        if (existingDemand == null) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND.value(), "Demanda não encontrada."), HttpStatus.NOT_FOUND);
        }

        // Preservar o userId da demanda existente
        demandDTO.setUserId(existingDemand.getUserId());

        DemandEntity demand = convertToEntity(demandDTO);
        demandService.updateDemand(demand);

        ApiResponse response = new ApiResponse(HttpStatus.OK.value(), "Demanda atualizada com sucesso.", demandDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteAllDemands() {
        demandService.deleteAllDemands();
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK.value(), "Todas as demandas foram deletadas com sucesso."), HttpStatus.OK);
    }

//    @GetMapping
//    public ResponseEntity<List<DemandDTO>> getAllDemands() {
//        List<DemandEntity> demands = demandService.getAllDemands();
//        if (demands.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        List<DemandDTO> demandDTOs = demands.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(demandDTOs);
//    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getDemandsByStatus(@PathVariable String status) {
        try {
            List<DemandEntity> demands = demandService.getDemandsByStatus(status);
            List<DemandDTO> demandDTOs = demands.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(demandDTOs);
        } catch (DemandNotFound ex) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        } catch (InvalidStatusException ex) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{demandId}/timer")
    public ResponseEntity<ApiResponse> updateDemandTimer(@PathVariable String demandId, @RequestBody TimerDTO timerDTO) {
        try {
            demandService.updateDemandTimer(demandId, timerDTO.getStartTime(), timerDTO.getEndTime());
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK.value(), "Timer da demanda atualizado com sucesso."), HttpStatus.OK);
        } catch (DemandNotFound ex) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDemands(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7); // Remove "Bearer "
        String userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);
        String groupId = jwtUtil.extractGroupId(token);

        List<DemandEntity> demands = demandService.getDemandsByUserAndSubordinates(userId, role, groupId);
        List<DemandDTO> demandDTOs = demands.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(demandDTOs);
    }

    // Métodos de conversão entre DTO e Entidade
    private DemandDTO convertToDTO(DemandEntity demand) {
        return DemandDTO.builder()
                .demandId(demand.getDemandId())
                .userId(demand.getUserId())
                .userIds(demand.getUserIds())
                .title(demand.getTitle())
                .description(demand.getDescription())
                .status(demand.getStatus())
                .startDate(demand.getStartDate())
                .endDate(demand.getEndDate())
                .type(demand.getType())
                .startTime(demand.getStartTime())
                .pauseTime(demand.getPauseTime())
                .totalDuration(demand.getTotalDuration())
                .autoStart(demand.isAutoStart())
                .statusDate(demand.getStatusDate())
                .build();
    }

    private DemandEntity convertToEntity(DemandDTO demandDTO) {
        return DemandEntity.builder()
                .demandId(demandDTO.getDemandId())
                .userId(demandDTO.getUserId())
                .userIds(demandDTO.getUserIds())
                .title(demandDTO.getTitle())
                .description(demandDTO.getDescription())
                .status(demandDTO.getStatus())
                .startDate(demandDTO.getStartDate())
                .endDate(demandDTO.getEndDate())
                .type(demandDTO.getType())
                .startTime(demandDTO.getStartTime())
                .pauseTime(demandDTO.getPauseTime())
                .totalDuration(demandDTO.getTotalDuration())
                .autoStart(demandDTO.isAutoStart())
                .statusDate(demandDTO.getStatusDate())
                .build();
    }
}