package me.appstockus.poosh_android.mvp.views.impls;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.natasa.progressviews.LineProgressBar;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import me.appstockus.poosh_android.R;
import me.appstockus.poosh_android.helpers.HttpHelper;
import me.appstockus.poosh_android.mvp.presenters.VoiceFragmentPresenter;
import me.appstockus.poosh_android.ui.views.RecordController;


public class VoiceFragment extends Fragment implements RecordController.Listener
{
    public static VoiceFragment newInstance() {
        return new VoiceFragment();
    }

    //consts:
    private static final int MAX_TIME = 15;
    private final int decreaseForce=100;
    private static final float PROGRESS_STEP = 100f / MAX_TIME;

    //views:
    private RecordController recordController;
    private LineProgressBar progressBar;

    //vars:
    private Timer timer;
    private float progress = 0f;


    /****************************************
     *              Lifecycle
     ***************************************/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_voice,
                container,
                false
        );

        recordController    = (RecordController) view.findViewById(R.id.fragment_voice_recordController);
        progressBar         = (LineProgressBar) view.findViewById(R.id.fragment_voice_progressBar);

        recordController.onClickListener = this;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(recordController == null || !recordController.isActive())
            progressBar.setProgress(0);
    }

    private void showEffectsActivity() {
        startActivity(
                new Intent(getActivity(), EffectsActivity.class)
        );
    }


    /****************************************
     *        RecordController.Listener
     ***************************************/

    @Override
    public void onRecordButtonClick(boolean isActive, final ImageView button) {
        if(timer != null)
            timer.cancel();

        MainActivity activity=(MainActivity)getActivity();

        if(isActive) {

            activity.startRecord();

            progress = 0f;
            progressBar.setProgress(0);

            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    progress += PROGRESS_STEP;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progress);
                            MainActivity activity=(MainActivity)getActivity();

                            if(progress >= 100) {
                                activity.stopMediaRecorder();
                                showEffectsActivity();
                                cancel();
                                recordController.animateToState(false);
                            }else
                                recordController.setForcePercent(activity.getAmplitude()/decreaseForce);

                        }
                    });
                }

            }, 0, 1000);
        }
        else {
            activity.stopMediaRecorder();
            showEffectsActivity();
        }
    }


}































