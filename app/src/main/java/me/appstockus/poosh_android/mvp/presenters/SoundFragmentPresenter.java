package me.appstockus.poosh_android.mvp.presenters;

import java.util.ArrayList;
import java.util.List;

import me.appstockus.poosh_android.models.SoundPack;

/**
 * Created by CITILINK-PC on 29.09.16.
 */
public interface SoundFragmentPresenter extends BasePresenter
{
    void onSoundsUploaded(final ArrayList<SoundPack> soundPacks);
    void onSoundsUploadingFailed();
}
