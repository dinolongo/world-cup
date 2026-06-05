package com.worldcup2026.exception;

public class FootballDataApiException extends RuntimeException {

    public FootballDataApiException(String message) {
        super(message);
    }

    public FootballDataApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
