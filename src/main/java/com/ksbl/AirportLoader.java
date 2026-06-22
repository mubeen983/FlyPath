package com.ksbl;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AirportLoader {

    final static String filePath = "airports.csv";

    final Map<String, Airport> airports;

    private static AirportLoader instance;



    private AirportLoader() {
         airports = new HashMap<>();
        loadAirports();
    }


    public static AirportLoader getInstance() {
        if (instance == null) {
            synchronized (AirportLoader.class) {
                if (instance == null) {
                    instance = new AirportLoader();
                }
            }
        }
        return instance;
    }

    private void loadAirports() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String iata = values[4];
                double lat = Double.parseDouble(values[values.length-2]);
                double lon = Double.parseDouble(values[values.length-1]);
                airports.put(iata, new Airport(values[0],values[1],values[2],values[3],values[4],values[5],values[6],lat,lon));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Map<String, Airport> getAirports(){
        return this.airports;
    }
}

