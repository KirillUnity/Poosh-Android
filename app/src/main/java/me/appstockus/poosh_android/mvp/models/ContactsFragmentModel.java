package me.appstockus.poosh_android.mvp.models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.appstockus.poosh_android.helpers.DataManager;
import me.appstockus.poosh_android.helpers.HttpHelper;
import me.appstockus.poosh_android.mvp.presenters.impls.ContactsFragmentPresenterImp;

/**
 * Created by CITILINK-PC on 24.03.2017.
 */

public class ContactsFragmentModel implements  HttpHelper.ResponseHandler {
    private Context context;
    private final ContactsFragmentPresenterImp presenter;

    public ContactsFragmentModel(ContactsFragmentPresenterImp presenter, Context context) {
        this.presenter = presenter;
        this.context=context;
    }


    /***************************************************
     *                     Methods
     **************************************************/
    public void inviteContacts(){
        try {
            HttpHelper.sendRequest(
                    new HttpHelper.RequestBuilder(context)
                            .setMethod(HttpHelper.RequestMethod.POST)
                            .addPath( new String[] { HttpHelper.pathUsers, DataManager.instance.init(this.context).userModel.id,  HttpHelper.pathContacts} )
                            .setParams(new JSONObject() {{
                                put("contacts",new JSONArray(){{
                                    put("79044496921");
                                }});
                            }})
                            .setHandler(this)
            );
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getContacts(){
        new HttpHelper.RequestBuilder(context)
                .setMethod(HttpHelper.RequestMethod.GET)
                .addPath( new String[] { HttpHelper.pathUsers, DataManager.instance.init(this.context).userModel.id,  HttpHelper.pathFriends} )
                .setHandler(this);
    }

    @Override
    public void onSuccessResponse(byte[] data, int code, String tag) {

    }

    @Override
    public void onErrorResponse(byte[] data, int code, String tag) {

    }
}
