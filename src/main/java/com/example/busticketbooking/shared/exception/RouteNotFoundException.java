package com.example.busticketbooking.shared.exception;

public class RouteNotFoundException extends NotFoundException {
    public RouteNotFoundException(String origin, String destination) {
        super("Route from " + origin + " to " + destination + " not found");
    }

    public RouteNotFoundException(long routeId) {
        super("Route with id '" + routeId + "' not found");
    }
}
