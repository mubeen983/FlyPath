package com.ksbl;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CurrencyConversion {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String BASE_URL = String.format("https://v6.exchangerate-api.com/v6/%s/pair/EUR/PKR",
            dotenv.get("EXCHANGE_RATE_API_KEY"));

    /**
         * Custom class to represent the API response
         */
        private record ExchangeRateResponse(String result, double conversionRate) {

        public boolean isSuccess() {
                return "success".equals(result);
            }

            /**
             * Parses the JSON string into an ExchangeRateResponse object
             *
             * @param json The JSON string from the API
             * @return ExchangeRateResponse object
             * @throws IllegalArgumentException if JSON parsing fails
             */
            public static ExchangeRateResponse fromJson(String json) {
                try {
                    // Simple JSON parsing without external libraries
                    json = json.replaceAll("\\s", "");
                    String result = extractValue(json, "result");
                    double rate = Double.parseDouble(extractValue(json, "conversion_rate"));
                    return new ExchangeRateResponse(result, rate);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Failed to parse API response: " + e.getMessage());
                }
            }

            private static String extractValue(String json, String key) {
                String searchKey = "\"" + key + "\":";
                int start = json.indexOf(searchKey) + searchKey.length();
                if (start == -1) {
                    throw new IllegalArgumentException("Key not found: " + key);
                }

                // Handle string values
                if (json.charAt(start) == '"') {
                    start++; // Skip opening quote
                    int end = json.indexOf('"', start);
                    return json.substring(start, end);
                }
                // Handle numeric values
                else {
                    int end = json.indexOf(',', start);
                    if (end == -1) {
                        end = json.indexOf('}', start);
                    }
                    return json.substring(start, end);
                }
            }
        }

    /**
     * Fetches the current EUR to PKR exchange rate from the API
     * @return The conversion rate
     * @throws IOException If there's an error calling the API
     */
    public double getExchangeRate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API call failed with status code: " + response.statusCode());
        }

        ExchangeRateResponse exchangeRate = ExchangeRateResponse.fromJson(response.body());

        if (!exchangeRate.isSuccess()) {
            throw new IOException("API call was not successful");
        }

        return exchangeRate.conversionRate();
    }

    /**
     * Converts an amount from EUR to PKR using the current exchange rate
     * @param eurAmount The amount in EUR to convert
     * @return The equivalent amount in PKR
     * @throws IOException If there's an error getting the exchange rate
     * @throws IllegalArgumentException If the amount is negative
     */
    public double convertEurToPkr(double eurAmount) throws IOException, InterruptedException {
        if (eurAmount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        double rate = getExchangeRate();
        return eurAmount * rate;
    }
}