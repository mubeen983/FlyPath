package com.ksbl;

import java.time.LocalDate;
import java.util.List;

public class FlightDataLoader {
    private final FlightAPIService apiService;
    private final FlightGraph graph;

    public FlightDataLoader(String apiKey, String apiSecret) {
        this.apiService = new FlightAPIService(apiKey, apiSecret);
        this.graph = new FlightGraph();
    }

    public FlightGraph loadFlightData(String origin, String destination, LocalDate date,int routes,boolean cheapest) {
        List<Flight> flights = apiService.searchFlights(origin, destination, date,routes);

        for (Flight flight : flights) {
            graph.addFlight(flight,cheapest);
        }

        return graph;
    }
}
