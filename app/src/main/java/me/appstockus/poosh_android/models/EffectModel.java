package me.appstockus.poosh_android.models;

import java.net.URL;

/**
 * Created by CITILINK-PC on 29.10.16.
 */
public class EffectModel
{
    public String title;
    public String soundUrl;
    public String previewUrl;

    public EffectModel(String title, String previewUrl, String soundUrl) {
        this.title = title;
        this.previewUrl = previewUrl;
        this.soundUrl = soundUrl;
    }
}
