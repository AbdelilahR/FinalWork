package com.example.currentplacedetailsonmap.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.Model.Chat;
import com.example.currentplacedetailsonmap.R;

import java.util.List;

//https://github.com/akshayejh/Lapit---Android-Firebase-Chat-App
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final int CHAT_END = 1;
    private static final int CHAT_START = 2;

    private List<Chat> mDataSet;
    private String mId;

    /**
     * Called when a view has been clicked.
     *
     * @param dataSet Message list
     * @param id      Device id
     */
    public ChatAdapter(List<Chat> dataSet, String id) {
        mDataSet = dataSet;
        mId = id;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == CHAT_END) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_end, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_start, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataSet.get(position).getId().equals(mId)) {
            return CHAT_END;
        }

        return CHAT_START;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = mDataSet.get(position);
        if (chat.getMessage() != null && holder.mTextView != null)
            holder.mTextView.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Inner Class for a recycler view
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) itemView.findViewById(R.id.tvMessage);
        }
    }
}
