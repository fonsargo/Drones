package com.musala.drones.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.drones.entity.Model;
import com.musala.drones.model.DroneDto;
import com.musala.drones.model.MedicationDto;
import com.musala.drones.model.OutputResult;
import com.musala.drones.service.DroneService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DroneController.class)
public class DroneControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DroneService droneService;

    ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Nested
    class RegisterDrone {

        @Test
        void shouldRegisterDrone() throws Exception {
            DroneDto droneDto = new DroneDto("12345", Model.Lightweight, 200, 55);
            OutputResult outputResult = OutputResult.success(droneDto);
            when(droneService.registerDrone(eq(droneDto))).thenReturn(outputResult);

            String content = Files.readString(Path.of("src/test/resources/data/drone.json"));
            String json = objectMapper.writeValueAsString(outputResult);
            mockMvc.perform(
                            put("/drone/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(json));
        }

        @Test
        void shouldReturnBadRequestIfRegisterExistsDrone() throws Exception {
            when(droneService.registerDrone(any())).thenThrow(new DuplicateKeyException("Constraint violation exception"));

            String content = Files.readString(Path.of("src/test/resources/data/drone.json"));
            OutputResult error = OutputResult.error("Entity with such key already exists : Constraint violation exception");
            String json = objectMapper.writeValueAsString(error);
            mockMvc.perform(
                            put("/drone/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(json));
        }

        @Test
        void shouldReturnBadRequestIfRegisterRequestSerialIsNotValid() throws Exception {
            String content = Files.readString(Path.of("src/test/resources/data/drone_serial_not_valid.json"));

            OutputResult error = OutputResult.error("Request is not valid: serialNumber: Serial number is mandatory; ");
            String json = objectMapper.writeValueAsString(error);
            mockMvc.perform(
                            put("/drone/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(json));
        }

        @Test
        void shouldReturnBadRequestIfRegisterRequestModelIsNotValid() throws Exception {
            String content = Files.readString(Path.of("src/test/resources/data/drone_model_not_valid.json"));

            OutputResult error = OutputResult.error("Request is not valid: model: Model is mandatory; ");
            String json = objectMapper.writeValueAsString(error);
            mockMvc.perform(
                            put("/drone/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(json));
        }

        @Test
        void shouldReturnBadRequestIfRegisterRequestWeightIsNotValid() throws Exception {
            String content = Files.readString(Path.of("src/test/resources/data/drone_weight_not_valid.json"));

            OutputResult error = OutputResult.error("Request is not valid: weightLimit: Weight limit can't be more than 500 gr; ");
            String json = objectMapper.writeValueAsString(error);
            mockMvc.perform(
                            put("/drone/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(json));
        }

        @Test
        void shouldReturnBadRequestIfRegisterRequestBatteryIsNotValid() throws Exception {
            String content = Files.readString(Path.of("src/test/resources/data/drone_battery_not_valid.json"));

            OutputResult error = OutputResult.error("Request is not valid: batteryCapacity: Battery capacity should be in percentage; ");
            String json = objectMapper.writeValueAsString(error);
            mockMvc.perform(
                            put("/drone/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(json));
        }
    }

    @Nested
    class LoadDrone {

        @Test
        void shouldLoadDrone() throws Exception {
            DroneDto droneDto = new DroneDto("12345", Model.Lightweight, 200, 55);
            List<MedicationDto> medications = List.of(
                    new MedicationDto("Name-1", 50, "CODE_01", "http://localhost/image1"),
                    new MedicationDto("Name-2", 150, "CODE_02", "http://localhost/image2")
            );

            OutputResult outputResult = OutputResult.success(droneDto);
            when(droneService.loadDrone(eq("12345"), eq(medications))).thenReturn(outputResult);

            String content = Files.readString(Path.of("src/test/resources/data/medications_list.json"));
            String json = objectMapper.writeValueAsString(outputResult);
            mockMvc.perform(
                            post("/drone/12345/load")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(json));
        }

        @Test
        void shouldReturnBadRequestIfNameNotValid() throws Exception {
            String content = Files.readString(Path.of("src/test/resources/data/medications_list_name_not_valid.json"));

            OutputResult error = OutputResult.error("Request is not valid: medications[1].name: Name allows only letters, numbers, ‘-‘, ‘_’; ");
            String json = objectMapper.writeValueAsString(error);
            mockMvc.perform(
                            post("/drone/12345/load")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(json));
        }

        @Test
        void shouldReturnBadRequestIfWeightNotValid() throws Exception {
            String content = Files.readString(Path.of("src/test/resources/data/medications_list_weight_not_valid.json"));

            OutputResult error = OutputResult.error("Request is not valid: medications[0].weight: Weight can't be negative; ");
            String json = objectMapper.writeValueAsString(error);
            mockMvc.perform(
                            post("/drone/12345/load")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(json));
        }

        @Test
        void shouldReturnBadRequestIfCodeNotValid() throws Exception {
            String content = Files.readString(Path.of("src/test/resources/data/medications_list_code_not_valid.json"));

            OutputResult error = OutputResult.error("Request is not valid: medications[0].code: Code allows only upper case letters, underscore and numbers; ");
            String json = objectMapper.writeValueAsString(error);
            mockMvc.perform(
                            post("/drone/12345/load")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(json));
        }

        @Test
        void shouldReturnBadRequestIfImageNotValid() throws Exception {
            String content = Files.readString(Path.of("src/test/resources/data/medications_list_image_not_valid.json"));

            OutputResult error = OutputResult.error("Request is not valid: medications[1].image: must be a valid URL; ");
            String json = objectMapper.writeValueAsString(error);
            mockMvc.perform(
                            post("/drone/12345/load")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(json));
        }
    }

    @Test
    void shouldReturnAvailableDrones() throws Exception {
        List<DroneDto> drones = List.of(
                new DroneDto("serial1", Model.Cruiserweight, 500, 75),
                new DroneDto("serial2", Model.Heavyweight, 400, 60),
                new DroneDto("serial3", Model.Middleweight, 300, 50),
                new DroneDto("serial4", Model.Lightweight, 150, 45)
        );
        OutputResult result = OutputResult.success(drones);
        when(droneService.getAvailableDronesForLoading()).thenReturn(result);

        String json = objectMapper.writeValueAsString(result);
        mockMvc.perform(get("/drone/available"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldReturnDroneMedications() throws Exception {
        String serial = "12345";
        List<MedicationDto> medications = List.of(
                new MedicationDto("Name-1", 50, "CODE_01", "http://localhost/image1"),
                new MedicationDto("Name-2", 150, "CODE_02", "http://localhost/image2")
        );
        OutputResult result = OutputResult.success(medications);
        when(droneService.getMedications(eq(serial))).thenReturn(result);

        String json = objectMapper.writeValueAsString(result);
        mockMvc.perform(get("/drone/" + serial + "/medications"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldReturnDroneBattery() throws Exception {
        String serial = "12345";
        OutputResult result = OutputResult.success(55);
        when(droneService.getBatteryLevel(eq(serial))).thenReturn(result);

        String json = objectMapper.writeValueAsString(result);
        mockMvc.perform(get("/drone/" + serial + "/battery"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

}
