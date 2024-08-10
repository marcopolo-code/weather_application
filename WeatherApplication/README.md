# JavaFX Weather Application - A simple weather forecasting app.

## Description
This is a simple weather forecasting application developed in Java using JavaFX. \
The application allows users to search for weather data by city name or by coordinates, view current weather, and see a 24-hour forecast. Additionally, it maintains a history of previous searches.

## Requirements

### Weather Data Implementation
- **WeatherData.java Class:**
  - Fetch weather data from a specified API.
  - Parse the data and provide it in a user-friendly format.
  - Handle errors and edge cases such as network issues or invalid input.

### User Interface
- **MainUI.java Class:**
  - A user-friendly interface to input city names or coordinates.
  - Display the current weather, a 24-hour forecast, and search history.
  - Allow users to switch between city name and coordinates input.

### Features
- **Search History:**
  - Stores previous searches with timestamps.
  - Displays search history with an option to reselect a previous search.

- **Temperature and Wind Speed Conversion:**
  - Clickable labels for converting temperature and wind speed units.

- **Customizable Forecast Display:**
  - Allows users to view a 24-hour forecast in 3-hour intervals.

### Installation
- Step 1: Clone the repository (Run the following commands in a Terminal application): \
  git clone https://github.com/marcopolo-code/chat-application.git \
  cd weather-application
- Step 2: Compile the source code using the following command: javac -d bin WeatherData.java MainUI.java
- Step 3: Run the application: java -cp bin MainUI
