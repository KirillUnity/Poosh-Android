package me.appstockus.poosh_android.mvp.views.impls;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import me.appstockus.poosh_android.R;
import me.appstockus.poosh_android.helpers.DataManager;
import me.appstockus.poosh_android.models.SoundPack;
import me.appstockus.poosh_android.models.UserModel;
import me.appstockus.poosh_android.mvp.presenters.impls.ContactsFragmentPresenterImp;
import me.appstockus.poosh_android.mvp.presenters.impls.VoiceFraqmentPresenterImpl;
import me.appstockus.poosh_android.mvp.views.MainActivityView;
import me.appstockus.poosh_android.mvp.views.impls.ContactsActivity.Type;

/**
 * Created by CITILINK-PC on 26.10.16.
 */
public class MainActivity extends BaseActivity implements MainActivityView
{
    //views:
    private CoordinatorLayout layout;
    private ViewPager viewPager;
    private View logo;

    //vars:
    private SoundFragment soundFragment;
    private VoiceFragment voiceFragment;
    private final ArrayList<SoundPack> soundPacks = new ArrayList<>();


    private VoiceFraqmentPresenterImpl voicePresenter;

    private final int request=123;
    private final String[] permissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    /****************************************
     *              RecordSound
     ***************************************/

    public void startRecord(){
        voicePresenter.onVoiseRecord();
    }
    public void stopMediaRecorder() {
        voicePresenter.onVoiseUploaded();
        voicePresenter.getPooshId();
    }

    /****************************************
     *              Lifecycle
     ***************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTransition();
        setContentView(R.layout.activity_main);

//      Fabric.with(this, new Crashlytics());

        logo = findViewById(R.id.activity_main_logo);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);

        layout = (CoordinatorLayout) findViewById(R.id.activity_main_coordinator);

        viewPager = (ViewPager) findViewById(R.id.activity_main_container);
        if (viewPager != null) {
            viewPager.setAdapter(fragmentPagerAdapter);

            final TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_main_tabs);
            if (tabLayout != null)
                tabLayout.setupWithViewPager(viewPager);
        }

        if( ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,permissions,request);
        voicePresenter=new VoiceFraqmentPresenterImpl(this);
    //    ContactsFragmentPresenterImp contacts=new ContactsFragmentPresenterImp(this);
    //    contacts.getContacts();
    }

    public int getAmplitude(){
        return voicePresenter.getVoiseAmplitude();
    }

    @Override
    public void onBackPressed() { }

    public void initTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Slide transition = new Slide(Gravity.RIGHT);
            transition.setDuration(500);
            getWindow().setEnterTransition(transition);
        }
    }

    /****************************************
     *                Menu
     ***************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.menu_main_invite:
                break;

            case R.id.menu_main_mute:
                mute(this);
                break;

            case R.id.menu_main_restore_purchases:
                break;

            case R.id.menu_main_logout:
                logout(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private static void logout(MainActivity activity) {
        DataManager.instance.userModel = UserModel.emptyModel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(
                    new Intent(activity, RegistrationActivity.class),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity,
                            Pair.create( activity.logo, activity.getString(R.string.transition_logo) )
                    ).toBundle()
            );
        }
        else {
            activity.startActivity(
                    new Intent(activity, RegistrationActivity.class)
            );
        }
    }

    private static void mute(Activity activity) {
        activity.startActivity(
                new Intent(activity, ContactsActivity.class).putExtra(
                        ContactsActivity.EXTRA_TYPE,
                        Type.MUTE.value
                )
        );
    }


    /****************************************
     *          FragmentPagerAdapter
     ***************************************/

    private final FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter( getSupportFragmentManager() )
    {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;

                if(position == 0) {
                    if(voiceFragment == null)
                        voiceFragment = VoiceFragment.newInstance();

                    fragment = voiceFragment;
                }
                else {
                    if(soundFragment == null)
                        soundFragment = SoundFragment.newInstance(MainActivity.this);

                    fragment = soundFragment;
                }

                return fragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ?
                        getString(R.string.activity_main_tab_title_voice) :
                        getString(R.string.activity_main_tab_title_sound);
            }

    };



    /******************************************
     *            MainActivityView
     *****************************************/

    @Override
    public void showLoadingError() {
        Snackbar.make(layout, R.string.loading_error, Snackbar.LENGTH_SHORT).show();
    }


}
