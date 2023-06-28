package com.musala.drones.service;

import com.musala.drones.model.DroneDto;
import com.musala.drones.model.MedicationDto;
import com.musala.drones.model.OutputResult;

import java.util.List;

public interface DroneService {

    OutputResult registerDrone(DroneDto droneDto);

    OutputResult loadDrone(String serial, List<MedicationDto> medicationDtos);

    OutputResult getAvailableDronesForLoading();

    OutputResult getMedications(String serial);

    OutputResult getBatteryLevel(String serial);

}
