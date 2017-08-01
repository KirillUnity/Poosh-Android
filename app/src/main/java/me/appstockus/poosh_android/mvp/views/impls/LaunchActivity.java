package me.appstockus.poosh_android.mvp.views.impls;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import me.appstockus.poosh_android.R;
import me.appstockus.poosh_android.helpers.DataManager;
import me.appstockus.poosh_android.mvp.views.impls.MainActivity;
import me.appstockus.poosh_android.mvp.views.impls.RegistrationActivity;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }


    @Override
    protected void onResume() {
        super.onResume();

        startActivity(
                new Intent(
                        this,
                        DataManager.instance.init(this).userModel.id.equals("-1")
                                ? RegistrationActivity.class
                                : MainActivity.class
                )
        );
    }
}
