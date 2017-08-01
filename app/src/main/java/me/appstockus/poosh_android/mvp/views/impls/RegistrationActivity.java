package me.appstockus.poosh_android.mvp.views.impls;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import me.appstockus.poosh_android.R;
import me.appstockus.poosh_android.helpers.DataManager;
import me.appstockus.poosh_android.helpers.HttpHelper;
import me.appstockus.poosh_android.helpers.HttpHelper.RequestMethod;
import me.appstockus.poosh_android.helpers.HttpHelper.ResponseHandler;
import me.appstockus.poosh_android.models.UserModel;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener,
                                                                    TextWatcher,
                                                                    ResponseHandler
{
    //views:
    private CoordinatorLayout layout;
    private View logo;
    private View title;
    private EditText phoneField;
    private View fab;

    //vars:
    private String formattedPhone = "";
    private String phone = "";


    /****************************************
     *              Lifecycle
     ***************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Fabric.with(this, new Crashlytics());

        initTransition();

        layout      = (CoordinatorLayout) findViewById(R.id.activity_registration_coordinator);
        logo        = findViewById(R.id.activity_registration_logo);
        title       = findViewById(R.id.activity_registration_help);
        phoneField  = (EditText) findViewById(R.id.activity_registration_editText);

        phoneField.addTextChangedListener(this);

        fab = findViewById(R.id.activity_registration_next);
//        fab.setOnTouchListener(this);
        fab.setOnClickListener(this);

        startedAnimation();
    }

    @Override
    public void onBackPressed() { }


    /****************************************
     *                  UI
     ***************************************/

    public void initTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();

            final Slide exit = new Slide(Gravity.LEFT);
            exit.setDuration(500);
            window.setExitTransition(exit);

            window.getSharedElementReenterTransition().addListener(
                    new Transition.TransitionListener() {
                        private boolean enabled = true;

                        @Override
                        public void onTransitionStart(Transition transition) {
                            enabled = !enabled;
                        }

                        @Override
                        public void onTransitionEnd(Transition transition) {
                            if(enabled) {
                                fab.setRotationY(180f);
                                final ObjectAnimator rotation = ObjectAnimator.ofFloat(fab, "rotation", 0f, 180f);
                                rotation.setDuration(500);
                                rotation.start();
                            }
                        }

                        @Override
                        public void onTransitionCancel(Transition transition) { }

                        @Override
                        public void onTransitionPause(Transition transition) { }

                        @Override
                        public void onTransitionResume(Transition transition) { }
                    }
            );
        }
    }

    private void startedAnimation() {
        final Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.fade_from_bottom);
        final Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.fade_from_bottom);
        final Animation anim3 = AnimationUtils.loadAnimation(this, R.anim.fade_from_bottom);
        final Animation anim4 = AnimationUtils.loadAnimation(this, R.anim.fade_from_bottom);

        logo.startAnimation(anim1);
        title.startAnimation(anim2);
        phoneField.startAnimation(anim3);
        fab.startAnimation(anim4);
    }


    /****************************************
     *            TextWatcher
     ***************************************/

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s != null && s.length() > 0) {
            if (s.charAt(0) == '8') {
                s.replace(0, 1, "+7");
            }
            else if(s.charAt(0) == '7') {
                s.replace(0, 1, "+7");
            }
            if(s.charAt(0) == '+') {
                if(s.length() > 1 && s.charAt(1) != '7')
                    s.insert(1, "7");
            }
            else {
                s.insert(0, "+7");
            }
        }

        final int len = formattedPhone.length();
        formattedPhone = s.toString();

        if(s.length() >= len) {
            String formatted;
            String regex1 = "(\\+\\d)(\\d{3})";
            String regex2 = "(.+ )(\\d{3})$";
            String regex3 = "(.+\\-)(\\d{2})$";

            if (s.toString().matches(regex1)) {
                formatted = String.valueOf(s).replaceFirst(regex1, "$1 ($2) ");
                phoneField.setText(formatted);
                phoneField.setSelection(formatted.length());
            }
            else if (s.toString().matches(regex2)) {
                formatted = String.valueOf(s).replaceFirst(regex2, "$1$2-");
                phoneField.setText(formatted);
                phoneField.setSelection(formatted.length());
            }
            else if (s.toString().matches(regex3) && s.length() < 18) {
                formatted = String.valueOf(s).replaceFirst(regex3, "$1$2-");
                phoneField.setText(formatted);
                phoneField.setSelection(formatted.length());
            }
        }
    }


    /****************************************
     *              Actions
     ***************************************/

    @Override
    public void onClick(View v) {
        final AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(
                ObjectAnimator.ofFloat(fab, "scaleX", 1f, 0.95f),
                ObjectAnimator.ofFloat(fab, "scaleY", 1f, 0.95f),
                ObjectAnimator.ofFloat(fab, "scaleX", 0.95f, 1f),
                ObjectAnimator.ofFloat(fab, "scaleY", 0.95f, 1f)
        );

        animatorSet.start();

        if(phoneField.length() == 18) {
            try {
                phone = phoneField.getText().toString().replaceAll("^(\\+)|\\D+","");

                if(phone != null && phone.length() == 11)
                    phone = phoneField.getText().toString().replaceAll("^(\\+)|\\D+","");

                    HttpHelper.sendRequest(
                            new HttpHelper.RequestBuilder(this)
                                    .setMethod(RequestMethod.POST)
                                    .addPath( new String[] { "users" })
                                    .setParams(new JSONObject() {{
                                        put("phone", phone);
                                        put("device", 0);
                                        put("device_token", FirebaseInstanceId.getInstance().getToken());
                                    }})
                                    .setHandler(this)
                    );
            }
            catch (JSONException e) {
                e.printStackTrace();
                super.showSnackBar(layout, R.string.activity_registration_error_message);
            }
        }
    }

    private void showVerifyActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(
                    new Intent(this, VerifyActivity.class),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this,
                            Pair.create( logo, getString(R.string.transition_logo) ),
                            Pair.create( fab, getString(R.string.transition_fab_reg_ver) )
                    ).toBundle()
            );
        }
        else {
            startActivity(
                    new Intent(this, VerifyActivity.class)
            );
        }
    }


    /****************************************
     *            ResponseHandler
     ***************************************/

    @Override
    public void onSuccessResponse(byte[] data, int code, String tag) {
        if(code == HttpHelper.CODE_SUCCESS) {
            try {
                final JSONObject jUser = DataManager.dataAsJson(data).getJSONObject("payload").getJSONObject("user");
                final UserModel userModel = DataManager.instance.userModel;
                userModel.id = jUser.optString("id", "-1");
                userModel.phone = jUser.optString("phone", "");

                showVerifyActivity();
            }
            catch (JSONException e) {
                e.printStackTrace();
                super.showSnackBar(layout, R.string.activity_registration_error_message);
            }
        }
        else {
            super.showSnackBar(layout, R.string.activity_registration_error_message);
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


        super.showSnackBar(layout, R.string.activity_registration_error_message);
    }
}
