package com.demands.infraestructure.repositories;

import com.demands.infraestructure.entity.DemandEntity;
import com.demands.infraestructure.entity.DemandStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DemandRepository extends MongoRepository<DemandEntity, String> {
    List<DemandEntity> findByUserId(String userId);

    List<DemandEntity> findByStatus(DemandStatus status);

    List<DemandEntity> findByUserIdOrUserIdsContaining(String userId, String userIds);

}