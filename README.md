# ✈️ FLYPATH: OPTIMIZED FLIGHT ROUTE FINDER💱

## 📋 Overview

This Java-based **Flight Booking System** enables users to search for flights between two cities and choose their preferred flight mode (cheapest or shortest). The system also integrates **currency conversion** to convert flight prices from **EUR to PKR** using an external API.

The system interacts with flight data through an API, validates user input, and recommends flights based on user preferences. It also includes classes that load airport and airline data from CSV files to make the experience more dynamic.

---

## 🛠️ Features

### ✈️ **Flight Search**
- Select an **origin** and **destination country**.
- Fetches flight data via an external API.
- Choose between two flight options: 
  - **Cheapest Flight** 🏷️
  - **Shortest Flight** ⏱️

### 📅 **Date Validation**
- Ensures that the flight date is valid and not in the past ⏳.

### 💱 **Currency Conversion**
- Converts flight prices from **EUR** to **PKR** using a currency conversion API 💰.
- Displays flight prices in PKR 💸.

### 📜 **CSV Data Loading**
- Loads **airport** and **airline** data from **CSV files** for dynamic flight searching.
- Classes like **AirportLoader** and **AirlineLoader** handle data loading, making the system more flexible.

### ✅ **Input Validation**
- Validates all user inputs to ensure the system operates without errors 🔍.

### ⚠️ **Error Handling**
- Handles invalid inputs, API failures, and empty fields gracefully 🚨.

---

## 🛠️ Prerequisites

To run this project, you’ll need the following installed:

- **Java 11+**: The project is built in Java.
- **Maven (Optional)**: If you're using Maven, dependencies will be automatically managed.

---

## 🏁 Getting Started

### 1. **Clone the repository**:

```bash
git clone [https://github.com/yourusername/flight-booking-system.git](https://github.com/abdullahimran49/FlyPath.git)
```
2. Install dependencies (if using Maven):
```bash
mvn install
```
3. Run the program:
```bash
javac App.java
java App
```
## 🔄 **CSV Data Loading**

The system uses the following classes to load data from CSV files:

### 🛬 **AirportLoader**
- Loads and parses airport data from CSV files.
- Provides airport details like code and country.

### 🛫 **AirlineLoader**
- Loads and parses airline data from CSV files.
- Provides airline details like name, IATA code, and flights available.

### 📂 **CSV File Locations**
- Place the **airports.csv** and **airlines.csv** files in the project directory or specify their location in the program's configuration.

---

## 🔧 **Classes Overview**

Here are the key classes in the system:

- **FlightBookingSystem.java** - Main entry point to run the system and interact with users.
- **AirportLoader.java** - Handles the loading of airport data from the CSV file.
- **AirlineLoader.java** - Handles the loading of airline data from the CSV file.
- **CurrencyConverter.java** - Fetches real-time currency rates and converts EUR to PKR.
- **FlightSearch.java** - Handles the flight search logic, determining whether the user wants the cheapest or shortest flight.
- **UserInputValidator.java** - Ensures all inputs from the user are valid.

---

## 🛠️ **Tech Stack**

- **Java** 🟨
- **CSV File Parsing** 📂
- **External APIs** for flight data and currency conversion 🌐
- **Maven** (Optional) 📦

---

## 📚 **Example Usage**

1. **Launch the program**.
2. **Enter the origin and destination countries**.
3. **Select the flight mode** (cheapest or shortest).
4. **View the flight options**.
5. **Enter the flight date**.
6. **Get the converted price in PKR**.

---

