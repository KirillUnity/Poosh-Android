package me.appstockus.poosh_android.mvp.views.impls;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import me.appstockus.poosh_android.helpers.DataManager;

/**
 * Created by CITILINK-PC on 05.10.16.
 */
public class BaseActivity extends AppCompatActivity
{
    protected static void showSnackBar(CoordinatorLayout layout, String text) {
        Snackbar.make(layout, text, Snackbar.LENGTH_SHORT).show();
    }

    protected static void showSnackBar(CoordinatorLayout layout, int resText) {
        Snackbar.make(layout, resText, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        DataManager.instance.saveAll(this);
    }
}
