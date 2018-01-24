package com.example.monster.airgesture.ui.user;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.monster.airgesture.R;
import com.example.monster.airgesture.data.bean.User;
import com.example.monster.airgesture.ui.base.BaseAdapter;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Welkinshadow on 2018/1/18.
 */

public class UserAdapter extends BaseAdapter<User, UserAdapter.ViewHolder> {

    private final List<User> data;
    private final UserAdapter.OnItemClickListener listener;

    /**
     * 回调接口
     */
    interface OnItemClickListener {
        void onClickItem(User user);

        void onLongClickItem(User user);
    }

    public UserAdapter(List<User> data, OnItemClickListener listener) {
        super(data);
        this.data = data;
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_user_layout)
        LinearLayout linearLayout;
        @BindView(R.id.item_user_name)
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            linearLayout.setClickable(true);
            linearLayout.setLongClickable(true);
        }
    }

    @Override
    protected boolean areItemsTheSame(User oldItem, User newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    protected boolean areContentsTheSame(User oldItem, User newItem) {
        return oldItem.getName().equals(newItem.getName());
    }

    @Override
    protected User getFirst() {
        if (!data.isEmpty())
            return data.get(0);
        else
            return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final User user = data.get(position);
        if (user != null) {
            holder.textView.setText(user.getName());
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickItem(user);
                }
            });
            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClickItem(user);
                    return true;
                }
            });
        }
    }


}
