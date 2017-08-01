package me.appstockus.poosh_android.models;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import me.appstockus.poosh_android.helpers.DataManager;

/**
 * Created by CITILINK-PC on 06.10.16.
 */
public class SoundModel
{
    public final long id;
    public final String name;
    public final String imageURL;
    public final String audioURL;
    public String localAudioURL;


    public SoundModel(long id, String name, String imageURL, String audioURL, String localAudioURL) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.audioURL = audioURL;
        this.localAudioURL = localAudioURL;
    }

    public static SoundModel fromJSON(JSONObject jo) {
        try {
            return new SoundModel(
                    jo.getLong("id"),
                    jo.getString("name"),
                    jo.getString("image"),
                    jo.getString("url"),
                    ""
            );
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SoundModel fromCursor(Cursor cursor) {
        try {
            return new SoundModel(
                    Long.valueOf( cursor.getString( cursor.getColumnIndex( DataManager.TableSound.ID ) ) ),
                    cursor.getString( cursor.getColumnIndex( DataManager.TableSound.NAME        ) ),
                    cursor.getString( cursor.getColumnIndex( DataManager.TableSound.IMAGE       ) ),
                    cursor.getString( cursor.getColumnIndex( DataManager.TableSound.AUDIO       ) ),
                    cursor.getString( cursor.getColumnIndex( DataManager.TableSound.LOCAL_AUDIO ) )
            );
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
