package me.appstockus.poosh_android.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.appstockus.poosh_android.R;
import me.appstockus.poosh_android.models.UserModel;
import me.appstockus.poosh_android.helpers.LetterBitmap;
import me.appstockus.poosh_android.mvp.views.impls.ContactsActivity.*;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> implements View.OnClickListener
{
    //vars:
    private final Context context;
    private Type type;

    private final int IMAGE_SIZE;
    private final LetterBitmap letterBitmap;

    private final List<UserModel> items;
    private final HashMap<Integer, Boolean> selected;



    /****************************************
     *              Lifecycle
     ***************************************/

    public ContactsAdapter(Context context, Type type) {
        this.context = context;
        this.type = type;

        this.items = new ArrayList<>();
        this.selected = new HashMap<>();

        this.IMAGE_SIZE = (int) context.getResources().getDimension(R.dimen.contact_icon_size);
        this.letterBitmap = new LetterBitmap(context);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(
                R.layout.item_contact,
                parent,
                false
        );

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final UserModel item = items.get(position);

        final boolean sel = selected.get(position);
        final int icon = type == Type.MUTE ? R.drawable.ic_mute_white : R.drawable.ic_mute;

        if(sel && type == Type.SEND) {
            holder.avatar.setImageResource(R.drawable.button_check);

            holder.name.setTextColor(
                    context.getResources().getColor(R.color.white)
            );
        }
        else {
            holder.avatar.setImageBitmap(
                    letterBitmap.getLetterTile(item.name, item.surname, IMAGE_SIZE)
            );

            holder.name.setTextColor(
                    context.getResources().getColor(R.color.baseHint)
            );
        }

        holder.muteButton.setImageResource(
                item.mute ? icon : 0
        );

        holder.name.setText(
                new StringBuilder(item.name).append(' ').append(item.surname).toString()
        );

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View v) {
        final Integer position = (Integer) v.getTag();

        if(type == Type.MUTE) {
            final UserModel item = items.get(position);
            item.mute = !item.mute;
        }

        selected.put(
                position,
                !selected.get(position)
        );

        notifyItemChanged(position);
    }


    ///
    public void setData(List<UserModel> data) {
        items.clear();
        items.addAll(data);

        final int count = items.size();

        selected.clear();
        if(type == Type.SEND) {
            for (int i = 0; i < count; ++i)
                selected.put(i, false);
        }
        else {
            for (int i = 0; i < count; ++i)
                selected.put(i, items.get(i).mute);
        }

        notifyDataSetChanged();
    }


    /****************************************
     *           ContactViewHolder
     ***************************************/

    public class ContactViewHolder extends RecyclerView.ViewHolder
    {
        public ContactsAdapter adapter;

        public ImageView avatar;
        public ImageView muteButton;
        public TextView name;

        public ContactViewHolder(View itemView) {
            super(itemView);
            avatar      = (ImageView) itemView.findViewById(R.id.item_contact_avatar);
            muteButton  = (ImageView) itemView.findViewById(R.id.item_contact_mute);
            name        = (TextView) itemView.findViewById(R.id.item_contact_name);
        }
    }

}
