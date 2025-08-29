package com.example.song.microservice.advice;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ErrorResponseWithDetails {

    private String errorMessage;
    private Map<String, String> details;
    private String errorCode;

    public ErrorResponseWithDetails(String errorMessage, Map<String, String> details, String errorCode) {
        this.errorMessage = errorMessage;
        this.details = details;
        this.errorCode = errorCode;
    }
}
