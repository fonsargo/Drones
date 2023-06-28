package com.musala.drones.controller;

import com.musala.drones.model.DroneDto;
import com.musala.drones.model.MedicationList;
import com.musala.drones.model.OutputResult;
import com.musala.drones.service.DroneService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/drone")
public class DroneController {

    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    @PutMapping("/register")
    public OutputResult register(@Valid @RequestBody DroneDto drone) {
        return droneService.registerDrone(drone);
    }

    @PostMapping("/{serial}/load")
    public OutputResult loadDrone(@PathVariable("serial") String serial, @Valid @RequestBody MedicationList medicationList) {
        return droneService.loadDrone(serial, medicationList.getMedications());
    }

    @GetMapping("/available")
    public OutputResult availableDrones() {
        return droneService.getAvailableDronesForLoading();
    }

    @GetMapping("/{serial}/medications")
    public OutputResult getMedications(@PathVariable("serial") String serial) {
        return droneService.getMedications(serial);
    }

    @GetMapping("/{serial}/battery")
    public OutputResult getBatteryLevel(@PathVariable("serial") String serial) {
        return droneService.getBatteryLevel(serial);
    }

}
