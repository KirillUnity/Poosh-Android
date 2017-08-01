package me.appstockus.poosh_android.mvp.views.impls;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import me.appstockus.poosh_android.R;
import me.appstockus.poosh_android.models.EffectModel;
import me.appstockus.poosh_android.ui.adapters.SimpleAdapter;
import me.appstockus.poosh_android.mvp.views.impls.ContactsActivity.*;

/**
 * Created by CITILINK-PC on 29.10.16.
 */
public class EffectsActivity extends AppCompatActivity implements View.OnClickListener, SimpleAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_effects_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.activity_effects_fab).setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.activity_effects_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new GridLayoutManager(this, 3)
        );

        adapter = new SimpleAdapter(this);
        adapter.onItemClickListener = this;

        adapter.addItem(0, new SimpleAdapter.Item(
                new EffectModel("Original", null, null), 0
        ));
        adapter.addItem(1, new SimpleAdapter.Item(
                new EffectModel("Male", null, null), 0 )
        );
        adapter.addItem(2, new SimpleAdapter.Item(
                new EffectModel("Female", null, null), 0 )
        );

        for(int i = 3; i<=12; ++i) {
            adapter.addItem(i, new SimpleAdapter.Item(
                    new EffectModel("Effect"+i, null, null), 0)
            );
        }

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        showContactsActivity();
    }

    @Override
    public void onItemClick(int position) {

    }

    private void showContactsActivity() {
        startActivity(
                new Intent(this, ContactsActivity.class).putExtra(
                        ContactsActivity.EXTRA_TYPE,
                        Type.SEND.value
                )
        );
    }
}
