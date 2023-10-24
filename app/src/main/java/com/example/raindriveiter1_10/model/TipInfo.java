package com.example.raindriveiter1_10.model;

import android.os.Build;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class TipInfo {
    private int tipId;
    private String tipTextSimple;
    private String tipTextExplained;
    private int tipDrawable;

    public TipInfo(int tipId, String tipTextSimple, String tipTextExplained, int tipDrawable) {
        this.tipId = tipId;
        this.tipTextSimple = tipTextSimple;
        this.tipTextExplained = tipTextExplained;
        this.tipDrawable = tipDrawable;
    }

    public void setTipTextSimple(String tipTextSimple) {
        this.tipTextSimple = tipTextSimple;
    }

    public void setTipTextExplained(String tipTextExplained) {
        this.tipTextExplained = tipTextExplained;
    }

    public String getTipTextSimple() {
        return tipTextSimple;
    }

    public String getTipTextExplained() {
        return tipTextExplained;
    }

    public void setTipId(int tipId) {
        this.tipId = tipId;
    }

    public void setTipDrawable(int tipDrawable) {
        this.tipDrawable = tipDrawable;
    }

    public int getTipId() {
        return tipId;
    }

    public int getTipDrawable() {
        return tipDrawable;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setUpTip(TextView tvTip, ImageView imageButton) {
        tvTip.setText(tipTextSimple);
        imageButton.setTooltipText(tipTextExplained);
    }


}
