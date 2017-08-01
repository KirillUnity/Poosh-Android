package me.appstockus.poosh_android.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import me.appstockus.poosh_android.models.SoundModel;
import me.appstockus.poosh_android.models.SoundPack;
import me.appstockus.poosh_android.models.UserModel;

/**
 * Created by CITILINK-PC on 29.10.16.
 */
public class DataManager
{
    public static final String PREFERENCES = "poosh_pref";
    public static final String PREF_USER_MODEL = "user_model";

    public static final String ROOT_DIR = "/sdcard/Poosh/";
    public static final String DIR_SOUNDS = "Sounds/";
    public static final String DIR_EFFECTS= "Effects/";

    public static final String MP3 = "\\.mp3";


    /****************************************
     *             Variables
     ***************************************/

    public UserModel userModel;

    private DBHelper dbHelper;


    /****************************************
     *           Initialization
     ***************************************/

    public final static DataManager instance = new DataManager();

    private DataManager() { }

    public DataManager init(Context context) {
        userModel = getUserModel(context);
        dbHelper = new DBHelper(context);
        return this;
    }


    /****************************************
     *              Methods
     ***************************************/

    public void saveAll(Context context) {
        saveUserModel(context, userModel);
    }


    /****************************************
     *           Common-Static
     ***************************************/

    public static String pathToSound(long soundId, String extension) {
        return new StringBuilder(ROOT_DIR)
                .append(DIR_SOUNDS)
                .append("sound")
                .append(soundId)
                .append(extension)
                .toString();
    }

    public static JSONObject dataAsJson(byte []data) {
        JSONObject jo = null;

        try {
            jo = new JSONObject(
                    new String(data, "UTF-8")
            );
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return jo;
    }

    public static void saveUserModel(Context context, UserModel userModel) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(
                        PREF_USER_MODEL,
                        userModel.getJSON().toString()
                )
                .commit();
    }

    private static void removeUserModel(Context context) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .remove(PREF_USER_MODEL)
                .commit();
    }

    private static UserModel getUserModel(Context context) {
        try {
            return UserModel.modelFromJson(
                    new JSONObject(
                            context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(PREF_USER_MODEL, "")
                    )
            );
        }
        catch (JSONException e) {
            e.printStackTrace();
            return UserModel.emptyModel();
        }
    }


    /****************************************
     *                SQLite
     ***************************************/

    private class DBHelper extends SQLiteOpenHelper
    {
        public static final int VERSION = 1;
        public static final String NAME = "PooshDB";


        public DBHelper(Context context) {
            super(context, NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    new StringBuilder("CREATE TABLE ").append(TableSound.TABLE_NAME).append(" (")
                            .append(TableSound.ID)         .append(" TEXT PRIMARY KEY, ")
                            .append(TableSound.NAME)       .append(" TEXT, ")
                            .append(TableSound.IMAGE)      .append(" TEXT, ")
                            .append(TableSound.AUDIO)      .append(" TEXT, ")
                            .append(TableSound.PACK_NAME)  .append(" TEXT)")
                            .toString()
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    }

    //Tables:
    public final class TableSound
    {
        public static final String TABLE_NAME = "table_sounds";

        public static final String ID = "s_id";
        public static final String NAME = "s_name";
        public static final String IMAGE = "s_image";
        public static final String AUDIO = "s_audio";
        public static final String LOCAL_AUDIO = "s_local_audio";
        public static final String PACK_NAME = "s_pack_name";
    }

    //Methods:
    public void soundPackToDB(SoundPack pack) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        final ArrayList<SoundModel> sounds = pack.sounds;

        for(final SoundModel sound : sounds) {
            final ContentValues values = new ContentValues();
            values.put( TableSound.ID,          String.valueOf(sound.id)       );
            values.put( TableSound.NAME,        String.valueOf(sound.name)     );
            values.put( TableSound.IMAGE,       String.valueOf(sound.imageURL) );
            values.put( TableSound.AUDIO,       String.valueOf(sound.audioURL) );
            values.put( TableSound.LOCAL_AUDIO, String.valueOf(sound.audioURL) );

            db.insertWithOnConflict(
                    TableSound.TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
        }

        db.close();
    }

    public ArrayList<SoundModel> soundPacksFromBD() {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        final Cursor it = db.rawQuery(
                new StringBuilder("SELECT * FROM ").append(TableSound.TABLE_NAME)
                        .append("GROUP BY ").append(TableSound.PACK_NAME)
                        .append(" ORDER BY ").append(TableSound.PACK_NAME)
                        .toString(),
                null
        );

        final ArrayList<SoundModel> packs = new ArrayList<>();
        ArrayList<SoundModel> sounds = new ArrayList<>();

        while( it.moveToNext() ) {
            final SoundModel sound = SoundModel.fromCursor(it);

            if( sound != null ) {
//                if(sound)
            }
        }

        db.close();

        return packs;
    }
}