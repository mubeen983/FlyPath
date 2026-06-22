package com.ksbl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Airports {



    Map<String,Airport> airports;

    Airports(){
        airports = AirportLoader.getInstance().getAirports();
    }


    List<Airport> searchByCountry(String country){
        return airports.
                values().
                stream().
                filter((airport -> airport.country()
                        .toLowerCase().contains(country.toLowerCase()))).
                collect(Collectors.toSet()).
                stream().toList();
    }

}
