package me.appstockus.poosh_android.mvp.views.impls;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.appstockus.poosh_android.R;
import me.appstockus.poosh_android.models.SoundModel;
import me.appstockus.poosh_android.models.SoundPack;
import me.appstockus.poosh_android.mvp.presenters.impls.SoundFragmentPresenterImpl;
import me.appstockus.poosh_android.mvp.views.MainActivityView;
import me.appstockus.poosh_android.mvp.views.SoundFragmentView;
import me.appstockus.poosh_android.ui.adapters.SectionedGridRecyclerViewAdapter;
import me.appstockus.poosh_android.ui.adapters.SectionedGridRecyclerViewAdapter.*;
import me.appstockus.poosh_android.ui.adapters.SimpleAdapter;

public class SoundFragment extends Fragment implements SoundFragmentView,
                                                        SimpleAdapter.OnItemClickListener,
                                                        View.OnClickListener
{
    public static SoundFragment newInstance(MainActivityView mainActivityView) {
        final SoundFragment fragment = new SoundFragment();
        fragment.mainActivityView = mainActivityView;
        return fragment;
    }

    private MainActivityView mainActivityView;
    private SoundFragmentPresenterImpl presenter;

    //views:
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    //vars:
    private ArrayList<SoundPack> soundPacks;

    private SimpleAdapter adapter;
    private final List<SectionedGridRecyclerViewAdapter.Section> sections = new ArrayList<>();


    /****************************************
     *              Lifecycle
     ***************************************/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_sound,
                container,
                false
        );

        presenter = new SoundFragmentPresenterImpl(this, getContext());

        fab = (FloatingActionButton) view.findViewById(R.id.fragment_sound_fab);
        fab.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_sound_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new GridLayoutManager( getActivity(), 3 )
        );

        adapter = new SimpleAdapter( getActivity() );
        adapter.onItemClickListener = this;

        initAdapter();

        presenter.loadSounds();

        return view;
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        presenter = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        //todo: add purchases
        showContactsActivity();
    }

    private void showContactsActivity() {
        startActivity(
                new Intent(getActivity(), ContactsActivity.class).putExtra(
                        ContactsActivity.EXTRA_TYPE,
                        ContactsActivity.Type.SEND.value
                )
        );
    }


    /****************************************
     *        RecyclerViewAdapter
     ***************************************/

    public void initAdapter() {

    }

    public void updateData(ArrayList<SoundPack> soundPacks) {
        this.soundPacks = soundPacks;
        initAdapter();
    }


    /****************************************
     *          OnItemClickListener
     ***************************************/

    @Override
    public void onItemClick(int position) {
        adapter.updateProgress(position, 65f);
    }


    /***************************************************
     *          HttpHelper.ResponseHandler
     **************************************************/

    @Override
    public void showSoundPacks(ArrayList<Section> sections, ArrayList<SoundModel> items) {
        adapter.setSounds(items);

        final SectionedGridRecyclerViewAdapter.Section []dummy = new SectionedGridRecyclerViewAdapter.Section[ sections.size() ];

        final SectionedGridRecyclerViewAdapter sectionedAdapter = new SectionedGridRecyclerViewAdapter(
                getActivity(),
                R.layout.section_sound,
                R.id.section_sound_text,
                R.id.section_sound_buyButton,
                R.id.section_sound_separator,
                recyclerView,
                adapter
        );
        sectionedAdapter.setSections( sections.toArray(dummy) );

        recyclerView.setAdapter(sectionedAdapter);
    }

    @Override
    public void showLoadingError() {
        mainActivityView.showLoadingError();
    }
}
