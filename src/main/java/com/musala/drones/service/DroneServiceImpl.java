package com.musala.drones.service;

import com.musala.drones.entity.Drone;
import com.musala.drones.entity.Medication;
import com.musala.drones.entity.State;
import com.musala.drones.model.DroneDto;
import com.musala.drones.model.MedicationDto;
import com.musala.drones.model.OutputResult;
import com.musala.drones.repository.DroneRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DroneServiceImpl implements DroneService {

    private static final int LOW_BATTERY_LEVEL = 25;
    private final DroneRepository droneRepository;

    public DroneServiceImpl(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Override
    public OutputResult registerDrone(DroneDto droneDto) {
        Drone drone = new Drone(droneDto.getSerialNumber(), droneDto.getModel(), droneDto.getWeightLimit(), droneDto.getBatteryCapacity(), State.IDLE);
        Drone saved = droneRepository.save(drone);
        return OutputResult.success(DroneDto.fromDrone(saved));
    }

    @Override
    @Transactional
    public OutputResult loadDrone(String serial, List<MedicationDto> medicationDtos) {
        if (CollectionUtils.isEmpty(medicationDtos)) {
            return OutputResult.error("Medications list is empty");
        }

        Drone drone = droneRepository.findWithLockBySerialNumber(serial);
        if (drone == null) {
            return OutputResult.error("Can't find drone with serial number: " + serial);
        }
        if (drone.getState() != State.IDLE) {
            return OutputResult.error("Can't load drone in state: " + drone.getState() + ", it should be IDLE");
        }
        if (drone.getBatteryCapacity() < LOW_BATTERY_LEVEL) {
            return OutputResult.error("Can't load drone when it's battery level below " + LOW_BATTERY_LEVEL + ", actual level: " + drone.getBatteryCapacity());
        }

        int summedWeight = medicationDtos.stream().map(MedicationDto::getWeight).mapToInt(Integer::intValue).sum();
        if (summedWeight > drone.getWeightLimit()) {
            return OutputResult.error("Drone can't be loaded with weight: " + summedWeight + "gr, it can carry only: " + drone.getWeightLimit());
        }

        List<Medication> medications = medicationDtos.stream()
                .map(medicationDto -> new Medication(medicationDto.getName(), medicationDto.getWeight(), medicationDto.getCode(), medicationDto.getImage()))
                .collect(Collectors.toList());
        drone.setMedications(medications);
        drone.setState(State.LOADED);
        Drone saved = droneRepository.save(drone);
        return OutputResult.success(DroneDto.fromDrone(saved));
    }

    @Override
    public OutputResult getAvailableDronesForLoading() {
        List<DroneDto> drones = droneRepository.findByState(State.IDLE).stream()
                .filter(drone -> drone.getBatteryCapacity() >= LOW_BATTERY_LEVEL)
                .map(DroneDto::fromDrone)
                .collect(Collectors.toList());
        return OutputResult.success(drones);
    }

    @Override
    public OutputResult getMedications(String serial) {
        Drone drone = droneRepository.findBySerialNumber(serial);
        if (drone == null) {
            return OutputResult.error("Can't find drone with serial number: " + serial);
        }
        List<MedicationDto> medications = drone.getMedications().stream().map(MedicationDto::fromMedication).collect(Collectors.toList());
        return OutputResult.success(medications);
    }

    @Override
    public OutputResult getBatteryLevel(String serial) {
        Drone drone = droneRepository.findBySerialNumber(serial);
        if (drone == null) {
            return OutputResult.error("Can't find drone with serial number: " + serial);
        }
        return OutputResult.success(drone.getBatteryCapacity());
    }
}
