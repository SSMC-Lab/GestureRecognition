package com.example.monster.airgesture.ui.input;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.monster.airgesture.R;
import com.example.monster.airgesture.model.db.Word;
import com.example.monster.airgesture.ui.base.BaseAdapter;

import java.util.List;

/**
 * Created by WelkinShadow on 2017/10/31.
 */

public class WordAdapter<T extends Word> extends BaseAdapter<T, WordAdapter.ViewHolder> {

    private final List<T> datas;
    private final AdapterListener listener;
    private Context mContext;

    /** 回调接口 */
    interface AdapterListener {
        void onClickItem(Word word);

        void onLongClickItem(Word word);
    }

    public WordAdapter(List<T> datas, AdapterListener listener) {
        super(datas);
        this.datas = datas;
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.item_layout);
            textView = (TextView) itemView.findViewById(R.id.item_text);
            linearLayout.setClickable(true);
            linearLayout.setLongClickable(true);
        }
    }

    @Override
    protected boolean areItemsTheSame(T oldItem, T newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    protected boolean areContentsTheSame(T oldItem, T newItem) {
        return oldItem.getWord().equals(newItem.getWord());
    }

    @Override
    protected T getFirst() {
        if (!datas.isEmpty()){
            return datas.get(0);
        }else {
            return null;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        mContext = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final T item = datas.get(position);
        if (item != null) {
            holder.textView.setText(item.getWord());
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickItem(item);
                }
            });
            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClickItem(item);
                    return true;
                }
            });
        }
    }

}
