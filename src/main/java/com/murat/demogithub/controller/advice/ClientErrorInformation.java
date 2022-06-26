package com.murat.demogithub.controller.advice;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
public class ClientErrorInformation {

    private ZonedDateTime timestamp;
    private String message;
    private Map<String, Object> details;

    public ClientErrorInformation(String message) {
        this.timestamp = ZonedDateTime.now();
        this.message = message;
    }

    public ClientErrorInformation(String message, Map<String, Object> details) {
        this(message);
        this.details = details;
    }
}
