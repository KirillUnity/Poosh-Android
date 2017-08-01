package me.appstockus.poosh_android.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by CITILINK-PC on 06.10.16.
 */
public class SoundPack
{
    public final String name;
    public final ArrayList<SoundModel> sounds;

    public SoundPack(String name, ArrayList<SoundModel> sounds) {
        this.name = name;
        this.sounds = sounds;
    }

    public static SoundPack fromJSON(JSONObject jo) {
        final ArrayList<SoundModel> sounds = new ArrayList<>();

        try {
            final JSONArray jsounds = jo.getJSONArray("sounds");
            final int len = jsounds.length();

            for(int i = 0; i < len; ++i) {
                try {
                    sounds.add(
                            SoundModel.fromJSON(
                                    jsounds.getJSONObject(i)
                            )
                    );
                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            return new SoundPack(
                    jo.getString("name"),
                    sounds
            );
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
