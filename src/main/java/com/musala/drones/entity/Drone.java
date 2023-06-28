package com.musala.drones.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
public class Drone {

    @Id
    private Long id;
    private final String serialNumber;
    private final Model model;
    private final int weightLimit;
    private int batteryCapacity;
    private State state;
    private List<Medication> medications = new ArrayList<>();

    public Drone(String serialNumber, Model model, int weightLimit, int batteryCapacity, State state) {
        this.serialNumber = serialNumber;
        this.model = model;
        this.weightLimit = weightLimit;
        this.batteryCapacity = batteryCapacity;
        this.state = state;
    }
}
