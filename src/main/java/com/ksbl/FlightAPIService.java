package com.ksbl;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;


import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;


public class FlightAPIService {
    private final Amadeus amadeus;
    AirportLoader airportLoader;

    public FlightAPIService(String apiKey, String apiSecret) {
        this.amadeus = Amadeus
                .builder(apiKey, apiSecret)
                .build();
        this.airportLoader = AirportLoader.getInstance();
    }

    public List<Flight> searchFlights(String origin, String destination, LocalDate date,int routes) {
        List<Flight> flights = new ArrayList<>();

        try {
            FlightOfferSearch[] flightOffers = amadeus.shopping.flightOffersSearch.get(
                    Params.with("originLocationCode", origin)
                            .and("destinationLocationCode", destination)
                            .and("departureDate", date.toString())
                            .and("adults", 1)
                            .and("max", routes)
            );

            for (FlightOfferSearch offer : flightOffers) {
                double totalPrice = Double.parseDouble(offer.getPrice().getTotal());

                for (FlightOfferSearch.Itinerary itinerary : offer.getItineraries()) {
                    int totalDuration = parseDuration(itinerary.getDuration());

                    for (FlightOfferSearch.SearchSegment segment : itinerary.getSegments()) {
                        Flight flight = new Flight(
                                segment.getDeparture().getIataCode(),
                                segment.getArrival().getIataCode(),// Distance not needed
                                totalPrice/offer.getItineraries()[0].getSegments().length,  // Store total price
                                totalDuration/offer.getItineraries()[0].getSegments().length,// Store total duration
                                AirlineLoader.getInstance().getAirline(segment.getCarrierCode())
                        );
                        flights.add(flight);
                    }
                }
            }
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }

        return flights;
    }


    private int parseDuration(String duration) {
        // Remove PT prefix
        duration = duration.substring(2);

        int hours = 0;
        int minutes = 0;

        // Find hours
        int hIndex = duration.indexOf('H');
        if (hIndex != -1) {
            hours = Integer.parseInt(duration.substring(0, hIndex));
            duration = duration.substring(hIndex + 1);
        }

        // Find minutes
        int mIndex = duration.indexOf('M');
        if (mIndex != -1) {
            minutes = Integer.parseInt(duration.substring(0, mIndex));
        }

        return hours * 60 + minutes;  // Return total minutes
    }


}
