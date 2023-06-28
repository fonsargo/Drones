package com.musala.drones.entity;

import lombok.Data;

@Data
public class DronePartial {
    private String serialNumber;
    private int batteryCapacity;
    private State state;
}
