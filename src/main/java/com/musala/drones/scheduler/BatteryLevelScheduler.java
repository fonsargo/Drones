package com.musala.drones.scheduler;

import com.musala.drones.entity.DronePartial;
import com.musala.drones.repository.DroneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BatteryLevelScheduler {

    private final DroneRepository droneRepository;
    private final int batchSize;

    public BatteryLevelScheduler(DroneRepository droneRepository, @Value("${audit.battery.batchSize}") int batchSize) {
        this.droneRepository = droneRepository;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedRateString = "${audit.battery.period}")
    public void auditBatteryLevels() {
        Slice<DronePartial> slice = droneRepository.findAllPartialBy(Pageable.ofSize(batchSize));
        slice.get().forEach(this::logInfo);

        while(slice.hasNext()) {
            slice = droneRepository.findAllPartialBy(slice.nextPageable());
            slice.get().forEach(this::logInfo);
        }
    }

    private void logInfo(DronePartial drone) {
        log.info("Drone battery info: {} ", drone);
    }
}
