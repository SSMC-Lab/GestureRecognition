package com.example.monster.airgesture.ui.user;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.monster.airgesture.R;
import com.example.monster.airgesture.ui.base.BaseAdapter;
import com.example.monster.airgesture.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 字母的适配器
 * Created by Welkinshadow on 2018/1/21.
 */

public class LetterAdapter extends BaseAdapter<String, LetterAdapter.ViewHolder> {

    private final List<String> data;
    private final LetterAdapter.OnItemClickListener listener;

    /**
     * 回调接口
     */
    interface OnItemClickListener {
        void onClickItem(String letter,ViewHolder holder);

        void onLongClickItem(String letter,ViewHolder holder);
    }

    public LetterAdapter(List<String> data, OnItemClickListener listener) {
        super(data);
        this.data = data;
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        boolean isSelected = false;

        @BindView(R.id.item_letter_layout)
        LinearLayout linearLayout;
        @BindView(R.id.item_letter_text)
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            linearLayout.setClickable(true);
            linearLayout.setLongClickable(true);
        }
    }

    @Override
    protected boolean areItemsTheSame(String oldItem, String newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    protected boolean areContentsTheSame(String oldItem, String newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    protected String getFirst() {
        if (data!=null){
            return data.get(0);
        }
        return null;
    }

    @Override
    public LetterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_letter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LetterAdapter.ViewHolder holder, int position) {
        final String letter = data.get(position);
        if (!StringUtils.isEmpty(letter)) {
            holder.textView.setText(letter);
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickItem(letter,holder);
                }
            });
            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClickItem(letter,holder);
                    return true;
                }
            });
        }
    }
}
