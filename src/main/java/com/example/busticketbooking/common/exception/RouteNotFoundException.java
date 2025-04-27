package com.example.busticketbooking.common.exception;

public class RouteNotFoundException extends NotFoundException {
    public RouteNotFoundException(String origin, String destination) {
        super("Route from " + origin + " to " + destination + " not found");
    }
}
