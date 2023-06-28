package com.musala.drones.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OutputResult {

    private Status status;
    private Object data;
    private String message;

    public static OutputResult success(Object data) {
        return new OutputResult()
                .setStatus(Status.SUCCESS)
                .setData(data);
    }

    public static OutputResult error(String message) {
        return new OutputResult()
                .setStatus(Status.FAILED)
                .setMessage(message);
    }
}
