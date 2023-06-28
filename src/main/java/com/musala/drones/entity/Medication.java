package com.musala.drones.entity;

import lombok.Data;

@Data
public class Medication {

    private final String name;
    private final int weight;
    private final String code;
    private final String image; //url to image
}
