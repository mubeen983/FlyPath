package com.ksbl;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import io.github.cdimascio.dotenv.Dotenv;

public class App {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Load environment variables safely
            Dotenv dotenv = null;
            try {
                dotenv = Dotenv.load();
                if (dotenv == null) {
                    throw new IllegalStateException("Failed to load environment variables");
                }
            } catch (Exception e) {
                System.err.println("Error loading environment variables: " + e.getMessage());
                return;
            }

            // Initialize components
            Airports airports = new Airports();


            // Handle origin country input
            HashMap<String, String> originCountry = processOriginInput(airports, scanner);
            if (originCountry == null) return;

            // Handle destination country input
            HashMap<String, String> destinationCountry = processDestinationInput(airports, scanner);
            if (destinationCountry == null) return;

            // Handle date input
            LocalDate flightDate = processDateInput(scanner);
            if (flightDate == null) return;

            // Handle flight mode selection
            Boolean cheapest = processFlightModeSelection(scanner);
            if (cheapest == null) return;

            // Print flight summary
            printFlightSummary(originCountry, destinationCountry, flightDate, cheapest);

            // Process flight data
            processFlightData(dotenv, originCountry, destinationCountry, flightDate, cheapest);

        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }

    }

    private static HashMap<String, String> processOriginInput(Airports airports, Scanner scanner) {
        try {
            System.out.print("Enter the origin country, or search for the country if you don't know the exact official name: ");
            String origin = scanner.nextLine().trim();

            if (origin.isEmpty()) {
                System.out.println("Error: Origin country cannot be empty");
                return null;
            }

            List<Airport> originsList = airports.searchByCountry(origin);
            if (originsList.isEmpty()) {
                System.out.println("Error: No airports found for the specified origin country");
                return null;
            }

            List<String> distinctOriginCountries = getDistinctCountries(originsList);

            if (distinctOriginCountries.size() > 1) {
                return printCountries(originsList, distinctOriginCountries, scanner, true);
            } else {
                List<String> distinctRegions = getDistinctRegions(originsList);
                return printRegions(originsList, distinctRegions, distinctOriginCountries.get(0), scanner, true);
            }
        } catch (Exception e) {
            System.err.println("Error processing origin input: " + e.getMessage());
            return null;
        }
    }

    private static HashMap<String, String> processDestinationInput(Airports airports, Scanner scanner) {
        try {
            System.out.print("Enter the destination country, or search for the country if you don't know the exact official name: ");
            String destination = scanner.nextLine().trim();

            if (destination.isEmpty()) {
                System.out.println("Error: Destination country cannot be empty");
                return null;
            }

            List<Airport> destinationList = airports.searchByCountry(destination);
            if (destinationList.isEmpty()) {
                System.out.println("Error: No airports found for the specified destination country");
                return null;
            }

            List<String> distinctDestinationCountries = getDistinctCountries(destinationList);

            if (distinctDestinationCountries.size() > 1) {
                return printCountries(destinationList, distinctDestinationCountries, scanner, false);
            } else {
                List<String> distinctDestinationRegions = getDistinctRegions(destinationList);
                return printRegions(destinationList, distinctDestinationRegions, distinctDestinationCountries.get(0), scanner, false);
            }
        } catch (Exception e) {
            System.err.println("Error processing destination input: " + e.getMessage());
            return null;
        }
    }

    private static LocalDate processDateInput(Scanner scanner) {
        try {
            System.out.print("Enter the date you want to fly on (YYYY-MM-DD): ");
            String date = scanner.nextLine().trim();

            if (date.isEmpty()) {
                System.out.println("Error: Date cannot be empty");
                return null;
            }

            LocalDate flightDate = LocalDate.parse(date);
            LocalDate today = LocalDate.now();

            if (flightDate.isBefore(today)) {
                System.out.println("Error: Flight date cannot be in the past");
                return null;
            }

            return flightDate;
        } catch (DateTimeParseException e) {
            System.err.println("Error: Invalid date format. Please use YYYY-MM-DD format");
            return null;
        } catch (Exception e) {
            System.err.println("Error processing date input: " + e.getMessage());
            return null;
        }
    }

    private static Boolean processFlightModeSelection(Scanner scanner) {
        try {
            System.out.println("\nFlight Mode Selection:");
            System.out.println("1. Cheapest Flight");
            System.out.println("2. Shortest Flight");
            System.out.print("Select the flight mode you want to choose (1/2): ");

            if (!scanner.hasNextInt()) {
                System.out.println("Error: Please enter a valid number (1 or 2)");
                return null;
            }

            int mode = scanner.nextInt();
            scanner.nextLine(); // Clear the buffer

            if (mode != 1 && mode != 2) {
                System.out.println("Error: Invalid mode selection. Please choose 1 or 2");
                return null;
            }

            return mode == 1;
        } catch (Exception e) {
            System.err.println("Error processing flight mode selection: " + e.getMessage());
            return null;
        }
    }

    private static void printFlightSummary(HashMap<String, String> originCountry,
                                           HashMap<String, String> destinationCountry,
                                           LocalDate flightDate,
                                           boolean cheapest) {
        System.out.println("\nFLIGHT SUMMARY");
        System.out.println("+----------------------+----------------------+----------------------+");
        System.out.println("Origin Country: " + originCountry.get("country"));
        System.out.println("Destination Country: " + destinationCountry.get("country"));
        System.out.println("Date: " + flightDate);
        System.out.println("Cheapest: " + (cheapest ? "Yes" : "No"));
        System.out.println("Shortest: " + (!cheapest ? "Yes" : "No"));
        System.out.println("+----------------------+----------------------+----------------------+");
    }

    private static void processFlightData(Dotenv dotenv,
                                          HashMap<String, String> originCountry,
                                          HashMap<String, String> destinationCountry,
                                          LocalDate flightDate,
                                          boolean cheapest) {
        try {
            String apiKey = dotenv.get("API_KEY");
            String apiSecret = dotenv.get("API_SECRET");

            if (apiKey == null || apiSecret == null) {
                throw new IllegalStateException("API credentials not found in environment variables");
            }

            System.out.println("Getting the flights....");
            FlightDataLoader dataLoader = new FlightDataLoader(apiKey, apiSecret);
            FlightGraph graph = dataLoader.loadFlightData(originCountry.get("IATA"),
                    destinationCountry.get("IATA"),
                    flightDate,
                    100,  // routes limit
                    cheapest);

            if (cheapest) {
                Route cheapestRoute = graph.findCheapestRoute(originCountry.get("IATA"),
                        destinationCountry.get("IATA"));
                if (cheapestRoute == null || cheapestRoute.toString().isEmpty()) {
                    System.out.println("We're sorry but there are no flights available");
                } else {
                    System.out.println("The Cheapest Route for you will be: ");
                    System.out.println(cheapestRoute);
                }
            } else {
                Route shortestRoute = graph.findShortestRoute(originCountry.get("IATA"),
                        destinationCountry.get("IATA"));
                if (shortestRoute == null || shortestRoute.toString().isEmpty()) {
                    System.out.println("We're sorry but there are no flights available");
                } else {
                    System.out.println("The Shortest Route for you will be:");
                    System.out.println(shortestRoute);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing flight data: " + e.getMessage());
        }
    }


    public static List<String> getDistinctCountries(List<Airport> airports) {
        return airports.stream()
                .map(Airport::country)
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<String> getDistinctRegions(List<Airport> airports) {
        return airports.stream()
                .map(Airport::regionName)
                .distinct()
                .collect(Collectors.toList());
    }

    public static HashMap<String,String> printCountries(List<Airport> airports, List<String> distinctCountries, Scanner sc,boolean isOrigin) {
        System.out.println("+----------------------+----------------------+----------------------+");
        for (int i = 0; i < distinctCountries.size(); i++) {
            System.out.println("|- " + (i + 1) + ": " + distinctCountries.get(i));
        }



        System.out.print("Select your country: ");
        int state = sc.nextInt();
        if (state>distinctCountries.size()){
            throw new InputMismatchException("Please select a valid country");
        }
        sc.nextLine();
        List<String> distinctRegions = airports.stream()
                .filter(airport -> airport.country().equals(distinctCountries.get(state - 1)))
                .map(Airport::regionName)
                .distinct()
                .collect(Collectors.toList());
        return printRegions(airports, distinctRegions, distinctCountries.get(state-1),sc,isOrigin);
    }

    public static HashMap<String,String> printRegions(List<Airport> regionsAirports, List<String> distinctRegions,String country ,Scanner sc,boolean isOrigin) {
        System.out.println("Following are the regions in the "+country+": ");
        System.out.println("+----------------------+----------------------+----------------------+");
        for (int i = 0; i < distinctRegions.size(); i++) {
            System.out.println("|- " + (i + 1) + ": " + distinctRegions.get(i));
        }

        System.out.print("Select your state/region/province: ");
        int state = sc.nextInt();
        if (state>distinctRegions.size()){
            throw new InputMismatchException("Please select a valid region");
        }
        sc.nextLine();
        Set<Airport> cities = regionsAirports.stream()
                .filter(a -> a.regionName().equals(distinctRegions.get(state - 1)))
                .collect(Collectors.toSet());
        return printCities(new ArrayList<>(cities), distinctRegions.get(state-1) ,sc,isOrigin);
    }

    public static HashMap<String,String> printCities(List<Airport> airportsCities, String region ,Scanner sc,boolean isOrigin) {
        String[] cities = new String[airportsCities.size()];
        System.out.println("Following are the cities in " + region+": ");
        System.out.println("+----------------------+----------------------+----------------------+");
        for (int i = 0; i < airportsCities.size(); i++) {
            System.out.println("|- " + (i + 1) + ": " + airportsCities.get(i).city());
            cities[i] = airportsCities.get(i).city();
        }
        System.out.print("Select your city: ");
        int city = sc.nextInt();
        if (city>cities.length){
            throw new InputMismatchException("Please select a valid region");
        }
        sc.nextLine();
        Airport airport = airportsCities.stream()
                .filter(a -> a.city().equals(cities[city - 1]))
                .findFirst()
                .orElse(null);
        HashMap<String,String> flight = new HashMap<>();
        if (airport != null) {
            flight.put("IATA",airport.IATA());
            flight.put("country",airport.country());
            System.out.println("+----------------------+----------------------+----------------------+");
            System.out.println("|--+ Your flight will " + (isOrigin ? "take off from " : "land at ") + airport.airportName()+" +--|");

            System.out.println("+----------------------+----------------------+----------------------+");

        } else {
            System.out.println("No airports found in the selected city.");

        }
        return flight;
    }
}



