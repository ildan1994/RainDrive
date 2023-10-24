package com.example.raindriveiter1_10.utility;

import android.util.Log;

import com.example.raindriveiter1_10.model.Accident;
import com.example.raindriveiter1_10.model.Question;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * This class provides the static methods to parse JSON
 * result get from API
 */
public class JsonConverter {


    public static HashMap<String, String> currentTrafficIncidences(String jsonResult)
    {
        HashMap<String, String> results = new HashMap<>();


        return results;
    }

    /**
     * This is the method to convert the json result get
     * from open weather api into the hash map data structure
     * @param jsonResult the json object get from open weather api
     * @return
     */
    public static HashMap<String, String> currentWeather(String jsonResult)
    {
        HashMap<String, String> results = new HashMap<>();
        try
        {
            JSONObject jsonObject = new JSONObject(jsonResult);

            results.put("temp",jsonObject.getJSONObject("main").getString("temp"));
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            results.put("main", weatherObject.getString("main"));
            results.put("description", weatherObject.getString("description"));
            results.put("icon",weatherObject.getString("icon"));
            int id = weatherObject.getInt("id")/100;
            if(id == 5)
            {
                results.put("isRain", "true");
                JSONObject rainObject = jsonObject.getJSONObject("rain");
                results.put("rainmm", rainObject.getString("1h"));

            }
            else results.put("isRain", "false");


        }
        catch (Exception e)
        {}
        return results;
    }

    public static HashMap<String, String> currentTrafficFlow(String jsonResult)
    {
        HashMap<String, String> results = new HashMap<>();
        return results;
    }

    //the first of result is for rainfall intensity and the second is for
    public static float[] accidentRate(String jsonResult)
    {
        float[] results = new float[2];
        try
        {
            JSONArray jsonArray = new JSONArray(jsonResult);

            JSONObject weatherObject = jsonArray.getJSONObject(0);
            results[0] = Float.parseFloat(weatherObject.getString("rainfall_intensity"));
            results[1] = Float.parseFloat(weatherObject.getString("accident_rate"));
            Log.i(TAG, "result 0: " + results[0]);
            Log.i(TAG, "result 1: " + results[1]);



        }
        catch (Exception e)
        {}

        return results;
    }

    /**
     * This is the method to transform the user current location from
     * longitude and latitude into the address name from google map api
     * @param jsonResult the json object get from google map api
     * @return the hash map which store the name of user's current location
     */
    public static HashMap<String, String> locationDetail(String jsonResult)
    {
        HashMap<String, String> results = new HashMap<>();
        try
        {
            JSONObject jsonObject = new JSONObject(jsonResult);

            JSONArray resultArray = jsonObject.getJSONArray("results");
            JSONObject addressObject = resultArray.getJSONObject(0);
            JSONArray addressArray = addressObject.getJSONArray("address_components");
            results.put("locality", addressArray.getJSONObject(0).getString("long_name"));
        }
        catch (Exception e)
        {}

        return results;
    }


    public static List<Question> allQuestions(String jsonResult)
    {
        List<Question> results = new ArrayList<>();
        try
        {
            JSONArray questionArray = new JSONArray(jsonResult);
            for(int i = 0; i < questionArray.length(); i++)
            {
                JSONObject tempQuestion = questionArray.getJSONObject(i);
                Question question = new Question();
                question.setQuestion(tempQuestion.getString("q_content"));
                question.setOption1(tempQuestion.getString("q_op1"));
                question.setOption2(tempQuestion.getString("q_op2"));
                question.setOption3(tempQuestion.getString("q_op3"));
                question.setHint(tempQuestion.getString("q_h"));
                question.setAnswerNr(Integer.parseInt(tempQuestion.getString("q_a")));
                results.add(question);
            }
        }
        catch (Exception e)
        {}


        return results;
    }

    /**
     * This is the method that transform the Json object get from
     * backend into the Accident object
     * @param jsonResult the json object get from backend
     * @return the accident objects retrieve from backend
     */
    public static List<Accident> accidentsByLocation(String jsonResult)
    {
        List<Accident> accidents = new ArrayList<>();
        try {
            JSONArray accidentArray = new JSONArray(jsonResult);
            for(int i = 0; i < accidentArray.length(); i++)
            {
                JSONObject accidentJson = accidentArray.getJSONObject(i);
                Accident accident = new Accident();
                accident.setDate(accidentJson.getString("accident_date"));
                accident.setLatitude(accidentJson.getDouble("latitude"));
                accident.setLongitude(accidentJson.getDouble("longitude"));
                accident.setRainLevel(accidentJson.getDouble("rainfall"));
                accident.setInjuryLevel(accidentJson.getInt("injury_level"));
                accidents.add(accident);
            }

        }
        catch (Exception e)
        {}
        return accidents;
    }
}
