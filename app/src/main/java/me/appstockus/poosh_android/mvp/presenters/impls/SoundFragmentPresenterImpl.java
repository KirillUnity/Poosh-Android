package me.appstockus.poosh_android.mvp.presenters.impls;

import android.content.Context;

import java.util.ArrayList;

import me.appstockus.poosh_android.models.SoundModel;
import me.appstockus.poosh_android.models.SoundPack;
import me.appstockus.poosh_android.mvp.models.SoundFragmentModel;
import me.appstockus.poosh_android.mvp.presenters.SoundFragmentPresenter;
import me.appstockus.poosh_android.mvp.views.SoundFragmentView;
import me.appstockus.poosh_android.ui.adapters.SectionedGridRecyclerViewAdapter;
import me.appstockus.poosh_android.ui.adapters.SectionedGridRecyclerViewAdapter.*;

/**
 * Created by CITILINK-PC on 29.09.16.
 */
public class SoundFragmentPresenterImpl implements SoundFragmentPresenter
{
    private Context context;

    private SoundFragmentView view;
    private SoundFragmentModel model;


    public SoundFragmentPresenterImpl(SoundFragmentView view, Context context) {
        this.view = view;
        this.context = context;
        this.model = new SoundFragmentModel(this, context);
    }


    /***************************************************
     *                     Methods
     **************************************************/

    public void loadSounds() {
        model.loadSounds();
    }


    /***************************************************
     *             SoundFragmentPresenter
     **************************************************/

    @Override
    public void onDestroy() {
        model = null;
        view = null;
    }

    @Override
    public void onSoundsUploaded(ArrayList<SoundPack> soundPacks) {
        final ArrayList<Section> sections = new ArrayList<>();
        final ArrayList<SoundModel> items = new ArrayList<>();

        for(final SoundPack pack : soundPacks) {
            //sectins:
            sections.add(new Section(
                    items.size(),
                    pack.name
            ));

            //items:
            items.addAll( pack.sounds );
        }

        view.showSoundPacks(sections, items);
    }

    @Override
    public void onSoundsUploadingFailed() {
        view.showLoadingError();
    }
}
