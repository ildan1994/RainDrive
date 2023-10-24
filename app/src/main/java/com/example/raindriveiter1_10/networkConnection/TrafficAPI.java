package com.example.raindriveiter1_10.networkConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


/**
 *
 * The API class used to get traffic information from TomTom API
 */
public class TrafficAPI {

    private static final String BASE_URL = "https://api.tomtom.com/traffic/services/";
    private static final String API_KEY = "upaGeHGwALEwcEb1RpZvaABAO4DXa5pY";


    public String getTrafficByLocation(String longitude, String latitude)
    {

        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query = BASE_URL + "4/flowSegmentData/absolute/10/json?point=" + latitude + "%2C" + longitude
                + "&unit=KMPH&thickness=10&key=" + API_KEY;

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

    public String getTrafficIncidentByLocation(String longitude, String latitude)
    {
        URL url = null;
        HttpURLConnection connection = null;
        double centerLati = Double.valueOf(latitude);
        double centerLong = Double.valueOf(longitude);
        double upleftLati = centerLati + 0.03;
        double boRightLati = centerLati - 0.03;
        double upLeftLong = centerLong - 0.03;
        double boRightLong = centerLong + 0.03;
        String textResult = "";
        String query = BASE_URL + "4/incidentDetails/s3/" + upleftLati + "%2C" + upLeftLong
                + "%2C" + boRightLati + "%2C" + boRightLong + "/10/-1/json?projection=EPSG4326&key=" + API_KEY;

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
