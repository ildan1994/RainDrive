package com.example.raindriveiter1_10.utility;

import android.widget.ImageButton;
import android.widget.TextView;

import com.example.raindriveiter1_10.R;
import com.example.raindriveiter1_10.model.TipInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TipHelper {
    List<TipInfo> tips;
    public TipHelper() {
        tips = new ArrayList<TipInfo>();
    }

    public List<TipInfo> getTips() {
        return tips;
    }

    public void initializeTips() {
        tips.add(new TipInfo(1,"Remember to brake in advance and with less force.",
                "When you are driving during wet condition, " +
                        "you should brake in advance and with less force to avoid " +
                        "slamming on your brakes.", R.drawable.tip_1_pic));
        tips.add(new TipInfo(2, "Don't use mobile phones and cruise control for navigation.",
                "Your car can actually accelerate when set to cruise " +
                        "control in wet condition.", R.drawable.tip_2_pic));
        tips.add(new TipInfo(3, "You should drive through slowly encounter large puddles.",
                "During wet condition, avoid speeding as you might hydroplane due to slippery roads " +
                        " and speeding through large puddles you could splash pedestrians.", R.drawable.tip_3_pic));

        tips.add(new TipInfo(4, "You should avoid using high-beam lights.",
                "High-beam lights will reflect back at you off the water and " +
                        "it will distract you.", R.drawable.tip_4_pic));
        tips.add(new TipInfo(5, "Maintain a safe distance between cars.",
                        "Stopping your vehicle will be difficult when it is raining. " +
                                "Maintain a  several car distance between your car and other vehicles"
                                       , R.drawable.tip_5_pic));
        Collections.shuffle(tips);
    }

    public void setTips(List<TipInfo> tips) {
        this.tips = tips;
    }
}

