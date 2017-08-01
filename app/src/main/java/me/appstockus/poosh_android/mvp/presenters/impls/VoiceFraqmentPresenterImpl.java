package me.appstockus.poosh_android.mvp.presenters.impls;

import android.content.Context;
import android.media.MediaRecorder;

import me.appstockus.poosh_android.mvp.models.VoiceFragmentModel;
import me.appstockus.poosh_android.mvp.presenters.VoiceFragmentPresenter;
import me.appstockus.poosh_android.mvp.views.VoiceFragmentView;

/**
 * Created by CITILINK-PC on 22.03.2017.
 */

public class VoiceFraqmentPresenterImpl implements VoiceFragmentPresenter {
    private Context context;

    private VoiceFragmentView view;
    private VoiceFragmentModel model;

    public VoiceFraqmentPresenterImpl(Context _context){
      //  view = _view;
        context = _context;
        this.model =new VoiceFragmentModel(this,context);
    }

    /***************************************************
     *                     Methods
     **************************************************/

    public void getPooshId() {
        model.getPooshId();
    }

    public void voiceUploaded() {
       // model.voiceUploaded();
    }

    public int getVoiseAmplitude() {
        return 0;//model.mediaRecorder.getMaxAmplitude();
    }


    /****************************************
     *              RecordSound
     ***************************************/

    @Override
    public void onVoiseUploaded() {
        if (model.mediaRecorder != null) {
            try {
                model. mediaRecorder.stop();
                model.mediaRecorder.reset();
                model.mediaRecorder.release();
            }
            catch (Exception e) {
                e.printStackTrace();
            } finally {
                model.mediaRecorder = null;
            }
        }
    }

    @Override
    public void onVoiseRecord() {
      //  model.fileSoundPath;
       // model.mediaRecorder=null;
        try{
            model.mediaRecorder=new MediaRecorder();
            model.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            model.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            model.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            model.mediaRecorder.setOutputFile(model.fileSoundPath);
            model.mediaRecorder.prepare();
            model.mediaRecorder.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        model = null;
        view = null;
    }
}
