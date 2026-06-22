package com.ksbl;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Route {
    private final List<Flight> flights;
    private double totalPrice;
    private int totalDuration;

    public Route() {
        this.flights = new ArrayList<>();
        this.totalPrice = 0;
        this.totalDuration = 0;
    }

    public void addFlight(Flight flight) {
        flights.add(flight);
        totalPrice += flight.price();
        totalDuration += flight.duration();

    }

    // Getters
    public List<Flight>  getFlights() { return flights; }
    public double getTotalPrice() { return totalPrice; }

    public int getTotalDuration() { return totalDuration; }

    @Override
    public String toString() {
        CurrencyConversion currencyConversion = new CurrencyConversion();
        if (flights.isEmpty()){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Route: ");
        for (int i = 0; i < flights.size(); i++) {
            sb.append(flights.get(i).airline())
                    .append(" (")
                    .append(flights.get(i).source())
                    .append(" -> ")
                    .append(flights.get(i).destination())
                    .append(")");
            if (i < flights.size() - 1) {
                sb.append(" -> ");
            }
        }


        try {
            sb.append("\nTotal Price: PKR ").append(String.format("%.2f", currencyConversion.convertEurToPkr(totalPrice)));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


        // Format duration as HH:MM
        int hours = totalDuration / 60;
        int minutes = totalDuration % 60;
        sb.append("\nTotal Duration: ").append(String.format("%02d Hours and %02d Minutes", hours, minutes));

        return sb.toString();
    }
}
