package com.example.raindriveiter1_10.networkConnection;

import com.google.android.gms.maps.model.LatLng;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * API class used to call google API
 */
public class GoogleAPI {

    private static final String API_KEY = "AIzaSyBwve9W6mi2QH0-aDJa-w_eMJhxDGeJ97E";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";

    //This method is used to get location name by longitude and latitude
    public String getLocalNameByLatLng(String latitude, String longitude)
    {
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query = BASE_URL + latitude + "," + longitude + "&result_type=locality" + "&key=" + API_KEY;


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
