package com.berserx.dede;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton on 2016-09-19.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.BindingHolder> {

    public static int TYPE_PROFILE = 0;
    public static int TYPE_PEERS = 1;

    protected int type;
    private Context mContext;
    private Item item;
    private List<Item> items;

    public ItemAdapter(Context context, ArrayList<Item> items, int itemType) {
        super(context, 0, items);
        type = itemType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Item item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (type == TYPE_PROFILE) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_item, parent, false);
            }
        } else if (type == TYPE_PEERS){
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.peer_item, parent, false);
            }
        }
        // Lookup view for data population
        TextView alias = (TextView) convertView.findViewById(R.id.peer_alias);
        TextView match = (TextView) convertView.findViewById(R.id.peer_match);
        // Populate the data into the template view using the data object
        alias.setText(item.alias);
        match.setText(item.broadcast);
        // Return the completed view to render on screen
        return convertView;
    }

    public void onBindViewHolder(BindingHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            ItemCommentsHeaderBinding commentsHeaderBinding = (ItemCommentsHeaderBinding) holder.binding;
            commentsHeaderBinding.setViewModel(new CommentHeaderViewModel(mContext, mPost));
        } else {
            int actualPosition = (postHasText()) ? position - 1 : position;
            ItemCommentBinding commentsBinding = (ItemCommentBinding) holder.binding;
            mComments.get(actualPosition).isTopLevelComment = actualPosition == 0;
            commentsBinding.setViewModel(new CommentViewModel(mContext, mComments.get(actualPosition)));
        }
    }

    public int getItemCount() {
        return items.size();
    }

    public int getItemViewType() {
        return type;
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public BindingHolder(ItemProfileBinding binding) {
            super(binding.containerItem);
            this.binding = binding;
        }

        public BindingHolder(ItemPeerBinding binding) {
            super(binding.containerItem);
            this.binding = binding;
        }
    }
}
