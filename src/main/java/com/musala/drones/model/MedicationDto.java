package com.musala.drones.model;

import com.musala.drones.entity.Medication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDto {

    @Pattern(regexp = "^[\\w-_]+$", message = "Name allows only letters, numbers, ‘-‘, ‘_’")
    @NotBlank(message = "Name is mandatory")
    private String name;
    @Min(value = 0, message = "Weight can't be negative")
    @NotNull(message = "Weight is mandatory")
    private Integer weight;
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code allows only upper case letters, underscore and numbers")
    @NotBlank(message = "Code is mandatory")
    private String code;
    @URL
    @NotBlank(message = "Image url is mandatory")
    private String image;

    public static MedicationDto fromMedication(Medication medication) {
        return new MedicationDto(medication.getName(), medication.getWeight(), medication.getCode(), medication.getImage());
    }
}
