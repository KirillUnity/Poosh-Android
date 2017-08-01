package me.appstockus.poosh_android.mvp.views.impls;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.appstockus.poosh_android.R;
import me.appstockus.poosh_android.models.UserModel;
import me.appstockus.poosh_android.ui.adapters.ContactsAdapter;

/**
 * Created by CITILINK-PC on 30.10.16.
 */
public class ContactsActivity extends AppCompatActivity implements View.OnClickListener
{
    public static String EXTRA_TYPE = "type";


    //views:
    private RecyclerView recyclerView;


    //vars:
    private List<UserModel> contacts = new ArrayList<>();
    private ContactsAdapter adapter;
    private Type type;


    /****************************************
     *              Lifecycle
     ***************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        type = getIntent().getExtras().getInt(EXTRA_TYPE, 1) == 1 ? Type.SEND : Type.MUTE;

        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_contacts_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.activity_contacts_fab).setOnClickListener(this);

        contacts.add(new UserModel("1", "Dmitry", "Belousov", "wreebvf", null, false));
        contacts.add(new UserModel("2", "Nikich", "Velko", "wreebvf", null, true));
        contacts.add(new UserModel("3", "Alex", "Gontorev", "wreebvf", null, false));

        for(int i = 4; i < 15; ++i)
            contacts.add(new UserModel(String.valueOf(i), "Name"+i, "Surname"+i, "wreebvf", null, false));

        recyclerView = (RecyclerView) findViewById(R.id.activity_contacts_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );

        adapter = new ContactsAdapter(this, type);

        recyclerView.setAdapter(adapter);
        adapter.setData(contacts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_contacts_invite:
                inviteFriend();
                break;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        sendPoosh();
    }

    private void inviteFriend() {

    }

    private void sendPoosh() {
        showFinalActivity();
    }

    private void showFinalActivity() {
        startActivity(
                new Intent(this, FinalActivity.class)
        );
    }

    /****************************************
     *              Type
     ***************************************/

    public enum Type
    {
        SEND(1),
        MUTE(2), ;

        public final int value;

        Type(int type) { this.value = type; }
    }
}
