package me.appstockus.poosh_android.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CITILINK-PC on 01.10.16.
 */
public class UserModel
{
    public String id;
    public String token;
    public String name;
    public String surname;
    public String phone;
    public boolean mute;

    public UserModel(String id, String name, String surname, String phone, String token, boolean mute) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.token = token;
        this.mute = mute;
    }

    public static UserModel modelFromJson(JSONObject jo) {
        return new UserModel(
                jo.optString("id", "-1"),
                jo.optString("name", ""),
                jo.optString("surname", ""),
                jo.optString("phone", ""),
                jo.optString("token", ""),
                jo.optBoolean("mute", false)
        );
    }

    public JSONObject getJSON() {
        return new JSONObject() {{
            try {
                put("id", id);
                put("token", token);
                put("name", name);
                put("surname", surname);
                put("phone", phone);
                put("mute", mute);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }};
    }

    public static UserModel emptyModel() {
        return new UserModel("-1", "", "", "", "", false);
    }
}
