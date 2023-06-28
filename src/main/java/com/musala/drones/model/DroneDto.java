package com.musala.drones.model;

import com.musala.drones.entity.Drone;
import com.musala.drones.entity.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DroneDto {
    @NotBlank(message = "Serial number is mandatory")
    @Size(max = 100, message = "Serial number can have maximum 100 characters")
    private String serialNumber;
    @NotNull(message = "Model is mandatory")
    private Model model;
    @Max(value = 500, message = "Weight limit can't be more than 500 gr")
    @Min(value = 0, message = "Weight limit can't be negative")
    @NotNull(message = "Weight limit is mandatory")
    private Integer weightLimit;
    @Max(value = 100, message = "Battery capacity should be in percentage")
    @Min(value = 0, message = "Battery capacity should be in percentage")
    @NotNull(message = "Battery capacity is mandatory")
    private Integer batteryCapacity;

    public static DroneDto fromDrone(Drone drone) {
        return new DroneDto(drone.getSerialNumber(), drone.getModel(), drone.getWeightLimit(), drone.getBatteryCapacity());
    }
}
