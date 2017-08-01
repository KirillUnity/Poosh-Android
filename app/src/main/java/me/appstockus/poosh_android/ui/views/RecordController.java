package me.appstockus.poosh_android.ui.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import me.appstockus.poosh_android.R;

public class RecordController extends FrameLayout implements View.OnTouchListener {
    //Listener:
    public Listener onClickListener;

    //views:
    private TextView tvHelp;
    private TextView tvRecord;
    private FrameLayout buttonGroup;
    private ImageButton button;
    private ImageView voiceForce;

    //vars:
    private final int minSize         = (int) getResources().getDimension(R.dimen.record_button_min_size);
    private final int maxSize         = (int) getResources().getDimension(R.dimen.record_button_max_size);
    private final int standartOffset  = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
    private final float percent = (1f * maxSize / minSize - 1f) / 100f;

    private int tvHelpY0 = 0;
    private int tvHelpYn = 0;
    private int tvRecordY0 = 0;
    private int tvRecordYn = 0;

    private boolean isActive = false;


    public RecordController(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.record_view, this, true);

        tvHelp      = (TextView) findViewById(R.id.record_view_text_help);
        tvRecord    = (TextView) findViewById(R.id.record_view_text_recording);
        buttonGroup = (FrameLayout) findViewById(R.id.record_view_button_group);
        button      = (ImageButton) findViewById(R.id.record_view_button);
        voiceForce  = (ImageView) findViewById(R.id.record_view_voice_force);

       // button.setOnClickListener(this);
        button.setOnTouchListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if(hasWindowFocus)
            init();
    }

    private void init() {
        if(tvHelpY0 == 0) {
            tvHelpY0 = getHeight() - minSize - maxSize - tvHelp.getHeight();    //visible
            tvHelpYn = tvHelpY0 + maxSize / 2 + tvHelp.getHeight() / 2;         //invisible

            tvRecordY0 = tvHelpYn;                                  //invisible
            tvRecordYn = (getHeight() - minSize - maxSize) / 2;   //visivle

            tvHelp.setY(tvHelpY0);
            tvRecord.setY(tvRecordY0);

            tvHelp.setAlpha(1f);
            tvRecord.setAlpha(0f);

            ViewGroup.LayoutParams params = voiceForce.getLayoutParams();
            params.width = minSize;
            params.height = minSize;
            voiceForce.setLayoutParams(params);
        }
    }

//    @Override
//    public void onClick(View v) {
//        isActive = !isActive;
//
//        animateToState(isActive);
//
//        if(onClickListener != null)
//            onClickListener.onRecordButtonClick(isActive);
//
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            animateToState(true);
            if(onClickListener != null)
                onClickListener.onRecordButtonClick(true,voiceForce);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            animateToState(false);
            if(onClickListener != null)
                onClickListener.onRecordButtonClick(false,voiceForce);
        }
        return false;
    }


    /******************************************
     *              UIStates
     *****************************************/

    public void setForcePercent(int force) {
        float scale = 1f + percent * force;

        final AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(
                ObjectAnimator.ofFloat(voiceForce, SCALE_X, voiceForce.getScaleX(), scale),
                ObjectAnimator.ofFloat(voiceForce, SCALE_Y, voiceForce.getScaleY(), scale)
        );
        animSet.start();
    }

    public void animateToState(boolean active) {
        final AnimatorSet animSet = new AnimatorSet();

        if(active) {
            animSet.playTogether(
                    ObjectAnimator.ofFloat(tvHelp, TRANSLATION_Y, tvHelpY0, tvHelpYn),
                    ObjectAnimator.ofFloat(tvHelp, ALPHA, 1f, 0f),
                    ObjectAnimator.ofFloat(tvHelp, SCALE_X, 1.0f, 0.85f),
                    ObjectAnimator.ofFloat(tvHelp, SCALE_Y, 1.0f, 0.85f),

                    ObjectAnimator.ofFloat(tvRecord, TRANSLATION_Y, tvRecordY0, tvRecordYn),
                    ObjectAnimator.ofFloat(tvRecord, ALPHA, 0f, 1f),
                    ObjectAnimator.ofFloat(tvRecord, SCALE_X, 0.85f, 1.0f),
                    ObjectAnimator.ofFloat(tvRecord, SCALE_Y, 0.85f, 1.0f)
            );

            setForcePercent(100);
        }
        else {
            setForcePercent(0);

            animSet.playTogether(
                    ObjectAnimator.ofFloat(tvHelp, TRANSLATION_Y, tvHelpYn, tvHelpY0),
                    ObjectAnimator.ofFloat(tvHelp, ALPHA, 0f, 1f),
                    ObjectAnimator.ofFloat(tvHelp, SCALE_X, 0.8f, 1.0f),
                    ObjectAnimator.ofFloat(tvHelp, SCALE_Y, 0.8f, 1.0f),

                    ObjectAnimator.ofFloat(tvRecord, TRANSLATION_Y, tvRecordYn, tvRecordY0),
                    ObjectAnimator.ofFloat(tvRecord, ALPHA, 1f, 0f),
                    ObjectAnimator.ofFloat(tvRecord, SCALE_X, 1.0f, 0.8f),
                    ObjectAnimator.ofFloat(tvRecord, SCALE_Y, 1.0f, 0.8f)
            );
        }

        animSet.start();
    }

    public boolean isActive() {
        return isActive;
    }




    /******************************************
     *              Listener
     *****************************************/

    public interface Listener
    {
        void onRecordButtonClick(boolean isActive,ImageView button);
    }
}
