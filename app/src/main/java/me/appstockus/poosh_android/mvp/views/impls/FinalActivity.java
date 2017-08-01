package me.appstockus.poosh_android.mvp.views.impls;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import me.appstockus.poosh_android.R;

/**
 * Created by CITILINK-PC on 01.10.16.
 */
public class FinalActivity extends AppCompatActivity
{
    private FinalActivity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        self = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        startActivity(
                                new Intent(self, MainActivity.class)
                        );
                    }

                });

            }

        }, 2000);
    }

    @Override
    public void onBackPressed() { }

}
