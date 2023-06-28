package com.musala.drones.model;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class MedicationList {

    @Valid
    private List<MedicationDto> medications;
}
