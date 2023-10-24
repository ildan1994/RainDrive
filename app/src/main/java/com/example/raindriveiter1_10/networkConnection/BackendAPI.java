package com.example.raindriveiter1_10.networkConnection;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * API class that used to connect to backend server on ec2
 */
public class BackendAPI {

    private static final String BASE_URL = "http://ec2-54-144-87-53.compute-1.amazonaws.com:8080/";

    public String getAccidentRateByRainfallIntensity(int intensity)
    {
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query = BASE_URL + "intensity/" + intensity;


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

    public String getAllQuestions()
    {
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query = BASE_URL + "question/all" ;

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

    public String getAccidentByLocation(double longitude, double latitude)
    {
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query = BASE_URL + "Accident/" + longitude + "/" + latitude;

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
