package com.marcalbert;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private static String city = "";
    private static float latitude;
    private static float longitude;
    private static boolean isToggled;
    private static final String API_KEY = "916946f939889bb0974578b2447e7589";
    private static boolean isCelsius = true; // Default unit for temperature
    private static boolean isKPH = true;    // Default unit for wind speed
    private static StackPane stackPane;
    static float temp;
    static float windSpeed;
    static StackPane weatherPane = new StackPane();
    static private List<String> searchHistory = new ArrayList<>();
    static VBox historyBox;
    static Label historyTitleLabel, backToMainLabel;
    static ImageView conditionsIcon;
    ImageView tempIcon;
    ImageView humidityIcon;
    ImageView windIcon;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create text fields
        TextField cityTextField = new TextField();
        cityTextField.setPromptText("Enter City");

        TextField latitudeTextField = new TextField();
        latitudeTextField.setPromptText("Enter Latitude");

        TextField longitudeTextField = new TextField();
        longitudeTextField.setPromptText("Enter Longitude");

        // Create the toggle button
        Button toggleButton = new Button("Switch to Coordinates");

        // Create the bottom button
        Button submit = new Button("Submit");

        Button showHistoryButton = new Button("Show History");


        // Create a VBox to hold the text fields and buttons
        VBox cityBox = new VBox(10, cityTextField);
        cityBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(10, toggleButton, showHistoryButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox latLongBox = new VBox(10, latitudeTextField, longitudeTextField);
        latLongBox.setAlignment(Pos.CENTER);

        // Create a BorderPane to hold the VBox and buttons
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);
        borderPane.setCenter(cityBox); // Set initial view to cityBox
        borderPane.setBottom(submit);
        BorderPane.setAlignment(submit, Pos.CENTER);

        // Create a StackPane to hold the background image and the BorderPane
        stackPane = new StackPane();
        stackPane.getChildren().add(borderPane);

        historyBox = new VBox(10);
        historyBox.setAlignment(Pos.CENTER);
        historyBox.setVisible(false); // Initially hidden

        historyTitleLabel = new Label("Search History");
        historyTitleLabel.setFont(new Font(20));
        historyTitleLabel.setTextFill(Color.color(1, 1, 1));

        historyBox.getChildren().add(historyTitleLabel);

        backToMainLabel = new Label("Back to Main");

        backToMainLabel.setStyle("-fx-background-color: #007bff; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10 20; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand; " +
                "-fx-alignment: center; " +
                "-fx-pref-width: 200px; " +
                "-fx-pref-height: 40px; " +
                "-fx-margin: 20 0 0 0;");

        backToMainLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            historyBox.setVisible(false);
            borderPane.setCenter(cityBox);
            submit.setVisible(true);
            toggleButton.setVisible(true);
            showHistoryButton.setVisible(true);
        });

        historyBox.getChildren().add(backToMainLabel);

        stackPane.getChildren().add(historyBox);


        // Create a StackPane for weather information
        weatherPane = new StackPane();
        weatherPane.setVisible(false); // Initially hidden


        Label titleLabel = new Label();
        String whole = "Today's Weather for " + city;
        titleLabel.setText(whole);
        titleLabel.setFont(new Font(20));
        titleLabel.setTextFill(Color.color(1, 1, 1));

        weatherPane.getChildren().add(titleLabel);
        weatherPane.setAlignment(titleLabel, Pos.TOP_CENTER);

        showHistoryButton.setOnAction(e -> {
            borderPane.setCenter(historyBox);
            historyBox.setVisible(true);
            submit.setVisible(false);
            toggleButton.setVisible(false);
            showHistoryButton.setVisible(false);
        });


// Create labels and icons for weather information
        Label tempLabel = new Label();
        Label humidityLabel = new Label();
        Label windSpeedLabel = new Label();
        Label conditionsLabel = new Label();
        Label back = new Label();
        back.setText("Back");

        tempLabel.setTextFill(Color.color(1, 1, 1));
        humidityLabel.setTextFill(Color.color(1, 1, 1));
        windSpeedLabel.setTextFill(Color.color(1, 1, 1));
        conditionsLabel.setTextFill(Color.color(1, 1, 1));
        back.setTextFill(Color.color(1, 1, 1));


// Add event handlers for labels
        tempLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            isCelsius = !isCelsius;
            tempLabel.setText("Temperature: " + formatTemperature(temp));
        });

        windSpeedLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            isKPH = !isKPH;
            windSpeedLabel.setText("Wind Speed: " + formatWindSpeed(windSpeed));

        });

        back.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            // Hide the weatherPane
            weatherPane.setVisible(false);

            // Show the cityBox and reset UI elements
            borderPane.setCenter(cityBox);
            toggleButton.setText("Switch to Coordinates");
            isToggled = false;
            cityTextField.clear();
            latitudeTextField.clear();
            longitudeTextField.clear();

            // Make input fields and buttons visible again
            stackPane.lookupAll(".text-field").forEach(node -> node.setVisible(true));
            stackPane.lookupAll(".button").forEach(node -> node.setVisible(true));
        });

        tempIcon = new ImageView(new Image("temp.png"));
        humidityIcon = new ImageView(new Image("humidity.png"));
        windIcon = new ImageView(new Image("wind_speed.png"));
        conditionsIcon = new ImageView();

        // Apply the white color effect to each ImageView
        applyWhiteColorEffect(tempIcon);
        applyWhiteColorEffect(humidityIcon);
        applyWhiteColorEffect(windIcon);
        applyWhiteColorEffect(conditionsIcon);

// Resize icons to 50%
        tempIcon.setFitHeight(50);
        tempIcon.setFitWidth(50);

        humidityIcon.setFitHeight(50);
        humidityIcon.setFitWidth(50);

        windIcon.setFitHeight(50);
        windIcon.setFitWidth(50);

        conditionsIcon.setFitHeight(50);
        conditionsIcon.setFitWidth(50);

        // Style the back label to look like a button with margin
        back.setStyle("-fx-background-color: #007bff; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10 20; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand; " +
                "-fx-alignment: center; " +
                "-fx-pref-width: 100px; " +
                "-fx-pref-height: 40px; " +
                "-fx-margin: 20 0 0 0;"); // Add margin to move it down

        Label shortTermForecastLabel = new Label("Short-Term Forecast");

// Style the button similarly to the "Back" button
        shortTermForecastLabel.setStyle("-fx-background-color: #007bff; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10 20; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand; " +
                "-fx-alignment: center; " +
                "-fx-pref-width: 220px; " +
                "-fx-pref-height: 40px; " +
                "-fx-margin: 20 0 0 0;"); // Add margin to move it down

        // Add a new VBox for forecast data
        VBox forecastBox = new VBox(10);
        forecastBox.setAlignment(Pos.CENTER);
        forecastBox.setVisible(false); // Initially hidden

// Add forecastBox to stackPane
        stackPane.getChildren().add(forecastBox);

// Event handler for shortTermForecastLabel
        shortTermForecastLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            weatherPane.setVisible(false); // Hide the weatherPane
            forecastBox.getChildren().clear(); // Clear previous forecast data

            // Fetch and display the forecast data
            getForecastData(forecastBox);
        });


// Create VBox and add it to the StackPane
        VBox weatherBox = new VBox(10, tempIcon, tempLabel, humidityIcon, humidityLabel, windIcon, windSpeedLabel, conditionsIcon, conditionsLabel, back, shortTermForecastLabel);
        weatherBox.setAlignment(Pos.CENTER);


// Add VBox to StackPane
        weatherPane.getChildren().add(weatherBox);

// Add weatherPane to stackPane
        stackPane.getChildren().add(weatherPane);
        StackPane.setAlignment(weatherPane, Pos.CENTER); // Center the weatherPane inside

        // Update background image based on the time of day
        updateBackgroundImage(stackPane);

        // Toggle button functionality
        toggleButton.setOnAction(e -> {
            if (borderPane.getCenter() == cityBox) {
                borderPane.setCenter(latLongBox);
                toggleButton.setText("Switch to City");
                isToggled = true;
                latitudeTextField.clear();
                longitudeTextField.clear();
            } else {
                borderPane.setCenter(cityBox);
                toggleButton.setText("Switch to Coordinates");
                isToggled = false;
                cityTextField.clear();
            }
            updateBackgroundImage(stackPane); // Update background image when toggling
        });

        // Submit button functionality
        submit.setOnAction(e -> {
            if (!cityTextField.getText().isBlank()) {
                city = convertToTitleCaseIteratingChars(cityTextField.getText());
            } else if (!latitudeTextField.getText().isBlank() && !longitudeTextField.getText().isBlank()) {
                latitude = Float.parseFloat(latitudeTextField.getText());
                longitude = Float.parseFloat(longitudeTextField.getText());
            }
            getData(city, latitude, longitude, tempLabel, humidityLabel, windSpeedLabel, conditionsLabel, weatherPane);
        });

        // Create a Scene and set it to the stage
        Scene scene = new Scene(stackPane, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Weather Application");
        primaryStage.show();
    }

    private static String getFormattedTimestamp(String string) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
        return now.format(formatter);
    }

    private static void addToHistory(String searchDetails) {
        String timestamp = getFormattedTimestamp(LocalDateTime.now().toString());
        searchHistory.add(timestamp + " - " + searchDetails);
        updateHistoryPane();
    }

    private static void updateHistoryPane() {
        historyBox.getChildren().clear();
        historyBox.getChildren().add(historyTitleLabel);

        historyTitleLabel.setFont(new Font(20));
        historyTitleLabel.setTextFill(Color.color(1, 1, 1));

        for (String entry : searchHistory) {
            Label historyEntry = new Label(entry);
            historyEntry.setTextFill(Color.color(0.4, 0.4, 0.3));
            historyBox.getChildren().add(historyEntry);
        }

        historyBox.getChildren().add(backToMainLabel);
    }

    private void getForecastData(VBox forecastBox) {
        try {
            // Use the API URL for 3-hour interval forecasts (every 3 hours)
            String requestUrl = isToggled
                    ? MessageFormat.format("https://api.openweathermap.org/data/2.5/forecast?lat={0}&lon={1}&appid={2}&units=metric", latitude, longitude, API_KEY)
                    : MessageFormat.format("https://api.openweathermap.org/data/2.5/forecast?q={0}&appid={1}&units=metric", city, API_KEY);

            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                // Parse the response and display the forecast
                parseForecastResponse(content.toString(), forecastBox);

            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseForecastResponse(String response, VBox forecastBox) {
        try {
            // Parse the JSON response
            String[] forecasts = response.split("\"dt\":");

            // Create the title label
            Label forecastTitleLabel = new Label("24 Hour Forecast");
            forecastTitleLabel.setFont(new Font(20));
            forecastTitleLabel.setTextFill(Color.color(1, 1, 1));

// Create the back button
            Label backToWeatherLabel = new Label("Back to Weather");

// Style the back button similarly to other buttons
            backToWeatherLabel.setStyle("-fx-background-color: #007bff; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 10 20; " +
                    "-fx-border-radius: 5; " +
                    "-fx-background-radius: 5; " +
                    "-fx-font-size: 14px; " +
                    "-fx-cursor: hand; " +
                    "-fx-alignment: center; " +
                    "-fx-pref-width: 200px; " +
                    "-fx-pref-height: 40px; " +
                    "-fx-margin: 20 0 0 0;"); // Add margin to move it down

// Event handler for the back button
            backToWeatherLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {

                weatherPane.setVisible(true); // Show the weatherPane
                forecastBox.setVisible(false);
            });

            forecastBox.getChildren().add(forecastTitleLabel);

            for (int i = 1; i <= 8; i++) { // Display next 9 hours (3 sets of 3-hour forecasts)
                String tempStr = forecasts[i].split("\"temp\":")[1].split(",")[0];
                String humidityStr = forecasts[i].split("\"humidity\":")[1].split(",")[0];
                String windSpeedStr = forecasts[i].split("\"speed\":")[1].split(",")[0];
                String conditionStr = forecasts[i].split("\"description\":\"")[1].split("\"")[0];

                String dtTxt = forecasts[i].split("\"dt_txt\":\"")[1].split("\"")[0];

                Label forecastLabel = new Label(
                        "Time: " + dtTxt + "\n" +
                                "Temperature: " + formatTemperature(Float.parseFloat(tempStr)) + "\n" +
                                "Humidity: " + humidityStr + "%\n" +
                                "Wind Speed: " + formatWindSpeed(Float.parseFloat(windSpeedStr)) + "\n" +
                                "Conditions: " + convertToTitleCaseIteratingChars(conditionStr)
                );

                forecastLabel.setTextFill(Color.color(0.4, 0.4, 0.3)); // Set text color to white
                forecastBox.getChildren().add(forecastLabel);
            }

            forecastBox.getChildren().add(backToWeatherLabel);

            forecastBox.setVisible(true); // Show the forecastBox
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to apply color adjustment to ImageView
    private void applyWhiteColorEffect(ImageView imageView) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(0);          // Set hue to 0 to maintain the color balance
        colorAdjust.setSaturation(-1);  // Set saturation to -1 to make the image grayscale
        colorAdjust.setBrightness(1);   // Set brightness to 1 to make it fully white

        imageView.setEffect(colorAdjust);
    }

    private static void updateBackgroundImage(StackPane stackPane) {
        String imageUrl;
        LocalTime now = LocalTime.now();

        if (now.isAfter(LocalTime.of(5, 0)) && now.isBefore(LocalTime.of(11, 0))) {
            imageUrl = "https://thumbs.dreamstime.com/b/heavenly-abstract-summer-gentle-background-beautiful-picturesque-bright-majestic-dramatic-evening-morning-sky-sunset-dawn-154667506.jpg";
        } else if (now.isAfter(LocalTime.of(12, 0)) && now.isBefore(LocalTime.of(16, 0))) {
            imageUrl = "https://t3.ftcdn.net/jpg/02/42/06/98/360_F_242069835_qcNTHwRYkiX3ldDY36RKZzEkCgFs0YEg.jpg";
        } else if (now.isAfter(LocalTime.of(17, 0)) && now.isBefore(LocalTime.of(19, 0))) {
            imageUrl = "https://t3.ftcdn.net/jpg/06/17/50/64/360_F_617506464_5F5kKV6TyX6M86JB0Cyx8t44LFNkHN8F.jpg";
        } else {
            imageUrl = "https://static.vecteezy.com/system/resources/thumbnails/030/353/225/small_2x/beautiful-night-sky-background-ai-generated-photo.jpg";
        }

        try {
            Image image = new Image(imageUrl, true);
            BackgroundImage backgroundImage = new BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true)
            );
            stackPane.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getData(String city, float latitude, float longitude, Label tempLabel, Label humidityLabel, Label windSpeedLabel, Label conditionsLabel, Pane weatherPane) {
        try {
            String requestUrl = isToggled
                    ? MessageFormat.format("https://api.openweathermap.org/data/2.5/weather?lat={0}&lon={1}&appid={2}&units=metric", latitude, longitude, API_KEY)
                    : MessageFormat.format("https://api.openweathermap.org/data/2.5/weather?q={0}&appid={1}&units=metric", city, API_KEY);

            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                // Parse the response
                String response = content.toString();
                parseWeatherResponse(response, tempLabel, humidityLabel, windSpeedLabel, conditionsLabel);

                String searchDetails = isToggled
                        ? MessageFormat.format("Latitude: {0}, Longitude: {1}", latitude, longitude)
                        : "City: " + city;

                addToHistory(searchDetails);

                System.out.println(response);

                // Show the weather information and hide input fields
                weatherPane.setVisible(true);
                stackPane.lookupAll(".text-field").forEach(node -> node.setVisible(false));
                stackPane.lookupAll(".button").forEach(node -> node.setVisible(false));
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseWeatherResponse(String response, Label tempLabel, Label humidityLabel, Label windSpeedLabel, Label conditionsLabel) {
        try {
            // Parse the JSON response (use a library like org.json or Gson in a real application)
            // For simplicity, we'll use basic string manipulation here

            String tempStr = response.split("\"temp\":")[1].split(",")[0];
            String humidityStr = response.split("\"humidity\":")[1].split(",")[0];
            String windSpeedStr = response.split("\"speed\":")[1].split(",")[0];
            String conditionStr = response.split("\"description\":\"")[1].split("\"")[0];

            if (conditionStr.contains("thunderstorm")) {
                conditionsIcon.setImage(new Image("thunder.png"));
            } else if (conditionStr.contains("rain") || conditionStr.contains("drizzle")) {
                conditionsIcon.setImage(new Image("rain.png"));
            } else if (conditionStr.contains("snow")) {
                conditionsIcon.setImage(new Image("snow.png"));
            } else if (conditionStr.contains("clouds")) {
                conditionsIcon.setImage(new Image("clouds.png"));
            } else if (conditionStr.contains("tornado")) {
                conditionsIcon.setImage(new Image("tornado.png"));
            } else if (conditionStr.contains("volcanic")) {
                conditionsIcon.setImage(new Image("volcan.png"));
            } else if (conditionStr.contains("sand") || conditionStr.contains("dust")) {
                conditionsIcon.setImage(new Image("dusty.png"));
            } else if (conditionStr.contains("mist") || conditionStr.contains("smoke") || conditionStr.contains("haze") || conditionStr.contains("fog")) {
                conditionsIcon.setImage(new Image("fog.png"));
            } else if (conditionStr.contains("clear sky")) {
                conditionsIcon.setImage(new Image("clear_sky.png"));
            }


            temp = Float.parseFloat(tempStr);
            int humidity = Integer.parseInt(humidityStr);
            windSpeed = Float.parseFloat(windSpeedStr);

            // Display weather information
            tempLabel.setText("Temperature: " + formatTemperature(temp));
            humidityLabel.setText("Humidity: " + humidity + "%");
            windSpeedLabel.setText("Wind Speed: " + formatWindSpeed(windSpeed));
            conditionsLabel.setText("Conditions: " + convertToTitleCaseIteratingChars(conditionStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertToTitleCaseIteratingChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }

    private static String formatTemperature(float temp) {
        return isCelsius ? String.format("%.1f °C", temp) : String.format("%.1f °F", temp * 9 / 5 + 32);
    }

    private static String formatWindSpeed(float speed) {
        return isKPH ? String.format("%.1f KPH", speed * 3.6) : String.format("%.1f MPH", speed * 2.237);
    }

    // Method to show alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}


