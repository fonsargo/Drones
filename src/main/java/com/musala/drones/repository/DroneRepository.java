package com.musala.drones.repository;

import com.musala.drones.entity.Drone;
import com.musala.drones.entity.DronePartial;
import com.musala.drones.entity.State;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.relational.core.sql.LockMode;
import org.springframework.data.relational.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

import java.util.List;

public interface DroneRepository extends CrudRepository<Drone, String> {

    List<Drone> findByState(State state);

    @Nullable
    Drone findBySerialNumber(String serialNumber);

    @Nullable
    @Lock(LockMode.PESSIMISTIC_WRITE)
    Drone findWithLockBySerialNumber(String serialNumber);

    Slice<DronePartial> findAllPartialBy(Pageable pageable);
}
