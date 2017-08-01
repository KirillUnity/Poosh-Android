package me.appstockus.poosh_android.mvp.views;

import java.util.ArrayList;

import me.appstockus.poosh_android.models.SoundModel;
import me.appstockus.poosh_android.ui.adapters.SectionedGridRecyclerViewAdapter.Section;

public interface SoundFragmentView
{
    void showSoundPacks(ArrayList<Section> sections, ArrayList<SoundModel> items);
    void showLoadingError();
}
