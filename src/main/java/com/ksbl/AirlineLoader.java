package com.ksbl;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AirlineLoader {

    final static String filePath = "airlines.csv";

    final Map<String, Airline> airlines;

    private static AirlineLoader instance;



    private AirlineLoader() {
        airlines = new HashMap<>();
        loadAirlines();
    }


    public static AirlineLoader getInstance() {
        if (instance == null) {
            synchronized (AirlineLoader.class) {
                if (instance == null) {
                    instance = new AirlineLoader();
                }
            }
        }
        return instance;
    }

    private void loadAirlines() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String iata = values[1];
                airlines.put(iata, new Airline(values[0],values[1]));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getAirline(String carrierCode){
        return this.airlines.get(carrierCode).name();
    }
}
