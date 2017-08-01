package me.appstockus.poosh_android.mvp.views.impls;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.natasa.progressviews.CircleProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import me.appstockus.poosh_android.R;
import me.appstockus.poosh_android.helpers.DataManager;
import me.appstockus.poosh_android.helpers.HttpHelper;
import me.appstockus.poosh_android.helpers.HttpHelper.RequestMethod;
import me.appstockus.poosh_android.helpers.HttpHelper.ResponseHandler;


public class VerifyActivity extends BaseActivity implements View.OnClickListener, TextWatcher, ResponseHandler
{
    private static final int PERIOD = 4000;
    private static final int CODE_LENGTH = 4;

    private Timer timer;

    private EditText codeField;
    private ImageView arrow;
    private View logo;
    private CircleProgressBar progressBar;
    private FloatingActionButton fab;
    private CoordinatorLayout layout;

    private final float step = 100f / PERIOD;
    private float progress = 0;


    /****************************************
     *             Lifecycle
     ***************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        initTransition();

        layout      = (CoordinatorLayout) findViewById(R.id.activity_verify_coordinator);
        logo        = findViewById(R.id.activity_verify_logo);
        arrow       = (ImageView) findViewById(R.id.activity_verify_arrow);
        codeField   = (EditText) findViewById(R.id.activity_verify_editText);
        progressBar = (CircleProgressBar) findViewById(R.id.activity_verify_progressBar);
        fab         = (FloatingActionButton) findViewById(R.id.activity_verify_fab);

        fab.setVisibility(View.VISIBLE);

        progressBar.setStartPositionInDegrees(270);
        fab.setOnClickListener(this);
        codeField.addTextChangedListener(this);
        codeField.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter( CODE_LENGTH )
        });

        arrow.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        setFieldState(true);
        setFABState(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
    }


    /****************************************
     *                UI
     ***************************************/

    public void initTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();

            final Slide enter = new Slide(Gravity.RIGHT);
            enter.setDuration(500);
            window.setEnterTransition(enter);

            final Slide exit = new Slide(Gravity.LEFT);
            exit.setDuration(500);
            window.setExitTransition(exit);

            window.setReturnTransition(new Slide(Gravity.RIGHT));

            window.getSharedElementEnterTransition().addListener(
                    new Transition.TransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) { }

                        @Override
                        public void onTransitionEnd(Transition transition) {
                            final ObjectAnimator rotation = ObjectAnimator.ofFloat(fab, "rotation", 0f, 180f);
                            rotation.setStartDelay(100);
                            rotation.setDuration(500);

                            final ObjectAnimator alpha = ObjectAnimator.ofFloat(fab, "alpha", 1f, 0f);
                            alpha.setStartDelay(100);
                            alpha.setDuration(350);

                            final AnimatorSet animator = new AnimatorSet();
                            animator.playTogether(rotation, alpha);
                            animator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) { }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    startTimer();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) { }

                                @Override
                                public void onAnimationRepeat(Animator animation) { }
                            });
                            animator.start();
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

    private void startTimer() {
        setFABState(false);

        progress = 0;
        progressBar.setProgress(0);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progress += step;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);

                        if(progress >= 100) {
                            timer.cancel();
                            setFABState(true);
                        }
                    }
                });
            }

        }, 0, 1);
    }

    private void setFABState(boolean enabled) {
        fab.setVisibility(
                enabled ? View.VISIBLE : View.GONE
        );
        fab.setAlpha(1f);
    }

    private void setFieldState(boolean normal) {
        codeField.setBackgroundDrawable(
                getResources().getDrawable(
                        normal ? R.drawable.bubble_corner_light : R.drawable.bubble_corner_error
                )
        );

        codeField.setTextColor(
                normal ? Color.WHITE : getResources().getColor( R.color.colorReject )
        );

        if( !normal ) {
            codeField.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.vibration_horizontal)
            );
        }
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(fab.getVisibility() == View.VISIBLE)
            super.onBackPressed();
    }


    /****************************************
     *            TextWatcher
     ***************************************/

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length() >= CODE_LENGTH) {
            verify();
        }
        else {
            setFieldState(true);
        }
    }


    /****************************************
     *            Controller
     ***************************************/

    private void verify() {
        HttpHelper.sendRequest(
                new HttpHelper.RequestBuilder(this)
                .addPath(new String[] {
                        "users", DataManager.instance.userModel.id, "verify"
    })
                .setParams(new JSONObject() {{
                    try {
                        put("code", codeField.getText().toString());
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }})
                .setMethod(RequestMethod.POST)
                .setHandler(this)
        );
    }

    private void showMainActivity() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(
                    new Intent(this, MainActivity.class),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this,
                            Pair.create(
                                    logo,
                                    getString(R.string.transition_logo)
                            )
//                            ,
//                            Pair.create(
//                                    (View)fab,
//                                    getString(R.string.transition_fab_ver_record)
//                            )
                    ).toBundle()
            );
        }
        else {
            startActivity(
                    new Intent(this, MainActivity.class)
            );
        }
    }


    /****************************************
     *            ResponseHandler
     ***************************************/

    @Override
    public void onSuccessResponse(byte[] data, int code, String tag) {
        boolean success = false;

        if(code == HttpHelper.CODE_SUCCESS) {
            try {
                final String token = DataManager
                        .dataAsJson(data)
                        .getJSONObject("payload")
                        .getString("api_token");

                if(token != null) {
                    DataManager.instance.userModel.token = token;
                    success = true;
                    DataManager.saveUserModel(this,DataManager.instance.userModel);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(success) {
            setFieldState(true);
            showMainActivity();
        }
        else {
            setFieldState(false);
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

        Log.d(tag, data.toString());

        setFieldState(false);
    }
}
