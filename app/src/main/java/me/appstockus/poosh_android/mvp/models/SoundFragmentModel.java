package me.appstockus.poosh_android.mvp.models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.appstockus.poosh_android.helpers.DataManager;
import me.appstockus.poosh_android.helpers.HttpHelper;
import me.appstockus.poosh_android.models.SoundPack;
import me.appstockus.poosh_android.mvp.presenters.SoundFragmentPresenter;

/**
 * Created by CITILINK-PC on 29.10.16.
 */
public class SoundFragmentModel implements HttpHelper.ResponseHandler
{
    private Context context;
    private final SoundFragmentPresenter presenter;

    //vars:
    private final ArrayList<SoundPack> soundPacks = new ArrayList<>();


    public SoundFragmentModel(SoundFragmentPresenter presenter, Context context) {
        this.presenter = presenter;
    }


    /***************************************************
     *                     Methods
     **************************************************/

    public void loadSounds() {
        HttpHelper.sendRequest(
                new HttpHelper.RequestBuilder(context)
                        .setMethod(HttpHelper.RequestMethod.GET)
                        .addPath( new String[] { HttpHelper.pathSounds } )
                        .setTag(HttpHelper.pathSounds)
                        .setHandler(this)
        );
    }


    /***************************************************
     *          HttpHelper.ResponseHandler
     **************************************************/

    @Override
    public void onSuccessResponse(byte[] data, int code, String tag) {
        if( HttpHelper.pathSounds.equals(tag) ) {
            try {
                final JSONObject jo = DataManager.dataAsJson(data);
                final JSONArray packs = DataManager.dataAsJson(data).getJSONObject("payload").getJSONArray("packs");
                final int len = packs.length();

                for(int i = 0; i < len; ++i) {
                    final SoundPack pack = SoundPack.fromJSON(
                            packs.getJSONObject(i)
                    );

                    if(pack != null)
                        soundPacks.add(pack);
                }

                presenter.onSoundsUploaded(soundPacks);
            }
            catch (JSONException je) {
                je.printStackTrace();

                presenter.onSoundsUploadingFailed();
            }
        }
    }

    @Override
    public void onErrorResponse(byte[] data, int code, String tag) {
        if( HttpHelper.pathSounds.equals(tag) ) {
            presenter.onSoundsUploadingFailed();
        }
    }
}
