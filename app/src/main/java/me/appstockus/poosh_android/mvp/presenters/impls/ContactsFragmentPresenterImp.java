package me.appstockus.poosh_android.mvp.presenters.impls;

import android.content.Context;
import me.appstockus.poosh_android.mvp.models.ContactsFragmentModel;

/**
 * Created by CITILINK-PC on 24.03.2017.
 */

public class ContactsFragmentPresenterImp {
    private Context context;

   // private VoiceFragmentView view;
    private ContactsFragmentModel model;

    public ContactsFragmentPresenterImp(Context _context){
        //  view = _view;
        context = _context;
        this.model =new ContactsFragmentModel(this,context);
    }

    public void getContacts(){
        model.getContacts();
    }
}
