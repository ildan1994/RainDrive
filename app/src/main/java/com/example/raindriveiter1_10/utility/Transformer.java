package com.example.raindriveiter1_10.utility;

public class Transformer {

    //This method is to transform rainfall from mm to
    // intensity from 0-4
    // if the response is -1, it means there is no rainfall currently;
    public static int rainfallTransform(float rainfall)
    {
        int result = -1;
        if(rainfall >= 0.1 && rainfall <= 1)
            result = 0;
        if(rainfall > 1 && rainfall <= 10)
            result = 1;
        else if(rainfall > 10 && rainfall <= 20)
            result = 2;
        else if(rainfall > 20 && rainfall <= 30)
            result = 3;
        else if(rainfall > 30)
            result = 4;

        return result;
    }
}
