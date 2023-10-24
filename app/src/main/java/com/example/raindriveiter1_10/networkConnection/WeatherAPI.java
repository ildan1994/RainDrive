package com.example.raindriveiter1_10.networkConnection;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * The API class used to get current weather information
 */
public class WeatherAPI {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?";
    private static final String API_KEY = "31944191428c6c6899f8d818c6a0d7bc";

    public String getCurrentWeatherByLocation(String longitude, String latitude)
    {

        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query = BASE_URL + "lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;

        try
        {
            url = new URL(query);
            connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNextLine())
            {
                textResult += scanner.nextLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            connection.disconnect();
        }

        return textResult;
    }

    public String getCurrentWeatherByCityName(String cityName)
    {
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query = BASE_URL + "q=" + cityName+ "&appid=" + API_KEY;


        try
        {
            url = new URL(query);
            connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNextLine())
            {
                textResult += scanner.nextLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            connection.disconnect();
        }

        return textResult;
    }
}
