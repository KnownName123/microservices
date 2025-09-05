package com.tamirian.resource.advice;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import java.util.Map;

@Getter
@Setter
public class ErrorResponseWithDetails {

    private String errorMessage;
    private Map<String, String> details;
    private String errorCode;

    public ErrorResponseWithDetails() {
    }

    public ErrorResponseWithDetails(String errorMessage, Map<String, String> details, String errorCode) {
        this.errorMessage = errorMessage;
        this.details = details;
        this.errorCode = errorCode;
    }

    public ErrorResponseWithDetails(String errorMessage, String errorCode) {
        this(errorMessage, null, errorCode);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
