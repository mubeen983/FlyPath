package com.ksbl;

import java.util.*;

public class FlightGraph {
    private final Map<String, Map<String, Flight>> adjacencyList;

    public FlightGraph() {
        this.adjacencyList = new HashMap<>();
    }

    // In FlightGraph.java
    public void addFlight(Flight flight,boolean cheapest) {
        adjacencyList.putIfAbsent(flight.source(), new HashMap<>());
        Map<String, Flight> destinations = adjacencyList.get(flight.source());
        Flight existingFlight = destinations.get(flight.destination());
        if (cheapest){
            if (existingFlight == null || flight.price() < existingFlight.price()){
                destinations.put(flight.destination(),flight);
            }
        }else{
            if (existingFlight == null || flight.duration() < existingFlight.duration()) {
                destinations.put(flight.destination(), flight);
            }
        }


    }

    public Route findShortestRoute(String source, String destination) {
        return findOptimalRoute(source, destination, true);
    }

    public Route findCheapestRoute(String source, String destination) {
        return findOptimalRoute(source, destination, false);
    }

    private Route findOptimalRoute(String source, String destination, boolean useTime ) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(
                Comparator.comparingDouble(distances::get)
        );

        // Initialize distances
        for (String node : adjacencyList.keySet()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(source, 0.0);
        queue.add(source);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equals(destination)) {
                break;
            }

            if (!adjacencyList.containsKey(current)) {
                continue;
            }

            for (Map.Entry<String, Flight> neighbor : adjacencyList.get(current).entrySet()) {
                String next = neighbor.getKey();
                Flight flight = neighbor.getValue();
                double newDistance = distances.get(current) +
                        (useTime ? flight.duration() : flight.price());

                if (newDistance < distances.getOrDefault(next, Double.POSITIVE_INFINITY)) {
                    distances.put(next, newDistance);
                    previousNodes.put(next, current);
                    queue.add(next);
                }
            }
        }

        // Reconstruct route
        Route route = new Route();
        String current = destination;

        if (!previousNodes.containsKey(destination)) {
            return route; // Return empty route if no path exists
        }

        Stack<String> path = new Stack<>();
        while (current != null) {
            path.push(current);
            current = previousNodes.get(current);
        }

        String prev = path.pop();
        while (!path.isEmpty()) {
            String next = path.pop();
            route.addFlight(adjacencyList.get(prev).get(next));
            prev = next;
        }

        return route;
    }
}
