package me.appstockus.poosh_android.mvp.models;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.appstockus.poosh_android.R;
import me.appstockus.poosh_android.helpers.DataManager;
import me.appstockus.poosh_android.helpers.HttpHelper;
import me.appstockus.poosh_android.models.UserModel;
import me.appstockus.poosh_android.mvp.presenters.VoiceFragmentPresenter;

/**
 * Created by CITILINK-PC on 22.03.2017.
 */

public class VoiceFragmentModel implements HttpHelper.ResponseHandler {
    private Context context;
    private final VoiceFragmentPresenter presenter;

    //vars:
    public final String fileSoundPath;
    public final String fileTempSoundPath;
    public MediaRecorder mediaRecorder;

    boolean upload;

    public VoiceFragmentModel(VoiceFragmentPresenter presenter, Context context) {
        this.presenter = presenter;
        this.context=context;
        fileSoundPath = getFilename();
        fileTempSoundPath= getTempFilename();
    }

    private String getFilename() {
        File file = new File(this.context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),"AudioRecorder");
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3");
       // return ("/storage/emulated/0/Android/data/me.appstockus.poosh_android/files/Music/AudioRecorder"+"/1490352134128.mp3");
    }
    private String getTempFilename() {
        File file = new File(this.context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),"AudioRecorder");
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3");
     //   return ("/storage/emulated/0/Android/data/me.appstockus.poosh_android/files/Music/AudioRecorder"+"/149035213428.mp3");
    }


    /***************************************************
     *                     Methods
     **************************************************/

    public void getPooshId() {
        upload=false;
        try{
          HttpHelper.sendRequest(
                new HttpHelper.RequestBuilder(context)
                        .setMethod(HttpHelper.RequestMethod.POST)
                        .addPath( new String[] { HttpHelper.pathPooshes } )
                        .setParams(new JSONObject() {{
                            put("recipients",new JSONArray(){{
                                put("79044496921");
                            }});
                            put("type", 1);
                        }})
                        .setHandler(this)
          );
      }catch (JSONException e){
          e.printStackTrace();
      }
    }

    public void voiceUploaded(String pooshId){
        upload=true;
        try {
            HttpHelper.sendRequest(
                    new HttpHelper.RequestBuilder(context)
                            .setMethod(HttpHelper.RequestMethod.POST)
                            .addPath(new String[]{HttpHelper.pathPooshes,pooshId,HttpHelper.pathUpload})
                            .setFile(fileSoundPath)
                            .setHandler(this)
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }



    /***************************************************
     *          HttpHelper.ResponseHandler
     **************************************************/

    @Override
    public void onSuccessResponse(byte[] data, int code, String tag) {
         if(code == HttpHelper.CODE_SUCCESS&&!upload) {
            try {
                final JSONObject jUser = DataManager.dataAsJson(data).getJSONObject("payload").getJSONObject("poosh");
                final UserModel userModel = DataManager.instance.userModel;
                String pooshId = jUser.optString("id", "-1");
             //   int type = jUser.optString("type",1 );

                if(pooshId!=""){
                    voiceUploaded(pooshId);
                }
              //  showVerifyActivity();
            }
            catch (JSONException e) {
                e.printStackTrace();
                //super.showSnackBar(layout, R.string.activity_registration_error_message);
            }
        }
    }

    @Override
    public void onErrorResponse(byte[] data, int code, String tag) {
        switch (code) {
            case HttpHelper.CODE_ERROR_AUTH:
                Log.d(tag, "auth error");
                break;

            case HttpHelper.CODE_ERROR_VALIDATION:
                Log.d(tag, "validation error");
                break;

            default:
                break;
        }
//        final JSONObject jUser = DataManager.dataAsJson(data);
//        Log.d(tag, data.toString());

       // super.showSnackBar(layout, R.string.activity_registration_error_message);
     }
}
