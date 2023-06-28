package com.musala.drones.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.drones.DBConfiguration;
import com.musala.drones.entity.Drone;
import com.musala.drones.entity.Medication;
import com.musala.drones.entity.Model;
import com.musala.drones.entity.State;
import com.musala.drones.model.DroneDto;
import com.musala.drones.model.MedicationDto;
import com.musala.drones.model.OutputResult;
import com.musala.drones.model.Status;
import com.musala.drones.repository.DroneRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJdbcTest
@Import(DBConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DroneServiceTest {

    DroneRepository droneRepository;
    DroneService droneService;
    ObjectMapper objectMapper;

    @Autowired
    public DroneServiceTest(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
        this.droneService = new DroneServiceImpl(droneRepository);
        this.objectMapper = new ObjectMapper();
    }

    @Nested
    class RegisterDrone {

        @Test
        void shouldRegisterDrone() throws IOException {
            File file = new File("src/test/resources/data/drone.json");
            DroneDto droneDto = objectMapper.readValue(file, DroneDto.class);
            OutputResult outputResult = droneService.registerDrone(droneDto);

            assertEquals(Status.SUCCESS, outputResult.getStatus());
            Drone actual = droneRepository.findBySerialNumber(droneDto.getSerialNumber());
            assertNotNull(actual);
            assertEquals(droneDto, DroneDto.fromDrone(actual));
        }
    }

    @Nested
    class LoadDrone {

        @Test
        void shouldLoadDroneWithMedications() throws IOException {
            String serialNumber = "serial1";
            Drone drone = new Drone(serialNumber, Model.Cruiserweight, 400, 60, State.IDLE);
            droneRepository.save(drone);

            File file = new File("src/test/resources/data/medications.json");
            List<MedicationDto> medications = objectMapper.readValue(file, new TypeReference<>() {});
            OutputResult outputResult = droneService.loadDrone(serialNumber, medications);

            assertEquals(Status.SUCCESS, outputResult.getStatus());
            Drone actual = droneRepository.findBySerialNumber(serialNumber);
            assertNotNull(actual);
            drone.setState(State.LOADED);
            Assertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "medications")
                    .isEqualTo(drone);
            List<MedicationDto> actualMedications = actual.getMedications().stream()
                    .map(MedicationDto::fromMedication)
                    .collect(Collectors.toList());
            assertEquals(medications, actualMedications);
        }

        @Test
        void shouldReturnFailedIfMedicationsIsEmpty() {
            OutputResult outputResult = droneService.loadDrone("serial", Collections.emptyList());
            assertEquals(Status.FAILED, outputResult.getStatus());
            assertEquals("Medications list is empty", outputResult.getMessage());
        }

        @Test
        void shouldReturnFailedIfDroneIsNotExist() throws IOException {
            String serialNumber = "serial1";
            File file = new File("src/test/resources/data/medications.json");
            List<MedicationDto> medications = objectMapper.readValue(file, new TypeReference<>() {});
            OutputResult outputResult = droneService.loadDrone(serialNumber, medications);
            assertEquals(Status.FAILED, outputResult.getStatus());
            assertEquals("Can't find drone with serial number: " + serialNumber, outputResult.getMessage());
        }

        @Test
        void shouldReturnFailedIfDroneIsNotIdle() throws IOException {
            String serialNumber = "serial1";
            Drone drone = new Drone(serialNumber, Model.Cruiserweight, 400, 60, State.LOADED);
            droneRepository.save(drone);

            File file = new File("src/test/resources/data/medications.json");
            List<MedicationDto> medications = objectMapper.readValue(file, new TypeReference<>() {});
            OutputResult outputResult = droneService.loadDrone(serialNumber, medications);
            assertEquals(Status.FAILED, outputResult.getStatus());
            assertEquals("Can't load drone in state: LOADED, it should be IDLE", outputResult.getMessage());
        }

        @Test
        void shouldReturnFailedIfDroneHasLowBattery() throws IOException {
            String serialNumber = "serial1";
            Drone drone = new Drone(serialNumber, Model.Cruiserweight, 400, 6, State.IDLE);
            droneRepository.save(drone);

            File file = new File("src/test/resources/data/medications.json");
            List<MedicationDto> medications = objectMapper.readValue(file, new TypeReference<>() {});
            OutputResult outputResult = droneService.loadDrone(serialNumber, medications);
            assertEquals(Status.FAILED, outputResult.getStatus());
            assertEquals("Can't load drone when it's battery level below 25, actual level: 6", outputResult.getMessage());
        }

        @Test
        void shouldReturnFailedIfMedicationsWeightMoreThenLimit() throws IOException {
            String serialNumber = "serial1";
            Drone drone = new Drone(serialNumber, Model.Lightweight, 150, 60, State.IDLE);
            droneRepository.save(drone);

            File file = new File("src/test/resources/data/medications.json");
            List<MedicationDto> medications = objectMapper.readValue(file, new TypeReference<>() {});
            OutputResult outputResult = droneService.loadDrone(serialNumber, medications);
            assertEquals(Status.FAILED, outputResult.getStatus());
            assertEquals("Drone can't be loaded with weight: 200gr, it can carry only: 150", outputResult.getMessage());
        }
    }

   @Nested
   class GetAvailableDrones {

       @Test
       @SuppressWarnings("unchecked")
       void shouldGetAvailableDronesForLoading() {
           List<Drone> drones = List.of(
                   new Drone("serial1", Model.Lightweight, 150, 60, State.IDLE),
                   new Drone("serial2", Model.Middleweight, 300, 60, State.LOADED),
                   new Drone("serial3", Model.Cruiserweight, 400, 60, State.IDLE),
                   new Drone("serial4", Model.Heavyweight, 500, 10, State.IDLE),
                   new Drone("serial5", Model.Cruiserweight, 400, 60, State.IDLE),
                   new Drone("serial6", Model.Cruiserweight, 400, 60, State.DELIVERING)
           );
           droneRepository.saveAll(drones);

           OutputResult outputResult = droneService.getAvailableDronesForLoading();

           assertEquals(Status.SUCCESS, outputResult.getStatus());
           List<DroneDto> data = (List<DroneDto>) outputResult.getData();
           List<DroneDto> expectedDrones = Stream.of(drones.get(0), drones.get(2), drones.get(4))
                   .map(DroneDto::fromDrone)
                   .collect(Collectors.toList());
           Assertions.assertThat(data)
                   .containsExactlyInAnyOrderElementsOf(expectedDrones);
       }
   }

   @Nested
   class GetMedications {

       @Test
       @SuppressWarnings("unchecked")
       void shouldLoadMedications() {
           String serial = "serial1";
           Drone drone = new Drone(serial, Model.Middleweight, 300, 60, State.DELIVERED);
           List<Medication> medications = List.of(
                   new Medication("Name-1", 25, "CODE_01", "http://localhost/image1.jpg"),
                   new Medication("Name-2", 50, "CODE_02", "http://localhost/image2.jpg"),
                   new Medication("Name-3", 75, "CODE_03", "http://localhost/image3.jpg")
           );
           drone.setMedications(medications);
           droneRepository.save(drone);

           OutputResult outputResult = droneService.getMedications(serial);

           assertEquals(Status.SUCCESS, outputResult.getStatus());
           List<MedicationDto> data = (List<MedicationDto>) outputResult.getData();
           List<MedicationDto> expected = medications.stream().map(MedicationDto::fromMedication).collect(Collectors.toList());
           Assertions.assertThat(data)
                   .containsExactlyInAnyOrderElementsOf(expected);

       }

       @Test
       void shouldReturnFailedIfDroneNotFound() {
           OutputResult outputResult = droneService.getMedications("serial1");
           assertEquals(Status.FAILED, outputResult.getStatus());
           assertEquals("Can't find drone with serial number: serial1", outputResult.getMessage());
       }
   }


   @Nested
   class GetBatteryLevel {

        @Test
        void shouldReturnDroneBatteryLevel() {
            String serialNumber = "serial1";
            Drone drone = new Drone(serialNumber, Model.Lightweight, 150, 80, State.IDLE);
            droneRepository.save(drone);

            OutputResult outputResult = droneService.getBatteryLevel(serialNumber);
            assertEquals(Status.SUCCESS, outputResult.getStatus());
            assertEquals(80, outputResult.getData());
        }

       @Test
       void shouldReturnFailedIfDroneNotFound() {
           OutputResult outputResult = droneService.getBatteryLevel("serial1");
           assertEquals(Status.FAILED, outputResult.getStatus());
           assertEquals("Can't find drone with serial number: serial1", outputResult.getMessage());
       }
   }
}
