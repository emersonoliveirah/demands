package com.demands.infraestructure.exceptions;

public class DemandNotFound extends RuntimeException {
    public DemandNotFound(String message) {
        super(message);
    }
}