package me.appstockus.poosh_android.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.natasa.progressviews.CircleProgressBar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.appstockus.poosh_android.R;
import me.appstockus.poosh_android.models.EffectModel;
import me.appstockus.poosh_android.models.SoundModel;

/**
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder>
{
    private final Context mContext;
    private final List<Item> mItems;

    private SimpleViewHolder lastSelectedHolder;
    private int lastSelectedPosition = -1;
    private int currentSelectedPosition = 0;
    private float progress = 100;

    public OnItemClickListener onItemClickListener;


    public SimpleAdapter(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
    }


    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_sound,
                parent,
                false
        );

        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        final Item item = mItems.get(position);

        holder.title.setText( item.title );
        holder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, item.icon, 0);

        Picasso.with(mContext)
                .load(item.previewURL)
                .into(holder.preview);

        holder.tag = position;
        holder.adapter = this;

        holder.progressBar.setProgress(
                position == currentSelectedPosition ? progress : 0
        );

        holder.title.setTextColor(
                mContext.getResources().getColor(
                        currentSelectedPosition == position ? R.color.white : R.color.baseHint
                )
        );

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private void itemClicked(int position, SimpleViewHolder holder) {
        lastSelectedPosition = currentSelectedPosition;
        currentSelectedPosition = position;

        notifyDataSetChanged();

        lastSelectedHolder = holder;
       // lastSelectedHolder.preview.setBackgroundColor(Color.GREEN);
      //  holder.
       // item.
        if(onItemClickListener != null){
            onItemClickListener.onItemClick(position);
         //   onItemClickListener.
        }

    }

    public void setSounds(List<SoundModel> items) {
        mItems.clear();
        for(final SoundModel sound : items)
            mItems.add( new Item(sound) );
        notifyDataSetChanged();
    }

    public void setData(List<Item> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(int position, Item item) {
        mItems.add(position, item);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    public void updateProgress(int position, float progress) {
        this.progress = progress;
        notifyItemChanged(position);
    }


    /****************************************
     *             SimpleViewHolder
     ***************************************/

    public static class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final TextView title;
        public final CircularImageView preview;
        public final CircleProgressBar progressBar;

        public int tag;
        public SimpleAdapter adapter;


        public SimpleViewHolder(View view) {
            super(view);

            title       = (TextView) view.findViewById(R.id.item_sound_title);
            preview     = (CircularImageView) view.findViewById(R.id.item_sound_preview);
            progressBar = (CircleProgressBar) view.findViewById(R.id.item_sound_progressBar);

            progressBar.setStartPositionInDegrees(270);
            progressBar.setProgress(0);

            preview.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(adapter != null)
                adapter.itemClicked(tag, this);
        }
    }


    /****************************************
     *                 Item
     ***************************************/

    public static class Item
    {
        public String title;
        public String audioURL;
        public String previewURL;
        public int icon;

        public Item(EffectModel model, int icon) {
            this.title = model.title;
            this.previewURL = model.previewUrl;
            this.audioURL = model.previewUrl;
            this.icon = icon < 0 ? 0 : icon;
        }

        public Item(SoundModel model) {
            this.title = model.name;
            this.previewURL = model.imageURL;
            this.audioURL = model.audioURL;

            this.icon = 0; //todo
        }
    }


    /****************************************
     *          OnItemClickListener
     ***************************************/

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }
}