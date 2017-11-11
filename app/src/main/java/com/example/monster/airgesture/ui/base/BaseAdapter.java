package com.example.monster.airgesture.ui.base;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WelkinShadow on 2017/10/31.
 */

public abstract class BaseAdapter<T,V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    protected List<T> temp;//保存数据源副本
    protected List<T> datas;//数据源

    public BaseAdapter(List<T> datas) {
        this.datas = datas;
        temp = new ArrayList<>(datas);
    }

    protected abstract boolean areItemsTheSame(T oldItem, T newItem);
    protected abstract boolean areContentsTheSame(T oldItem, T newItem);
    protected abstract T getFirst();

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void notifyDiff(List<T> newDatas) {
        datas.clear();
        datas.addAll(newDatas);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return temp.size();
            }
            @Override
            public int getNewListSize() {
                return datas.size();
            }
            // 需要刷新整个 layout 时返回 true
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return BaseAdapter.this.areItemsTheSame(temp.get(oldItemPosition), datas.get(newItemPosition));
            }
            // 需要刷新个别 item 时返回 true
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return BaseAdapter.this.areContentsTheSame(temp.get(oldItemPosition), datas.get(newItemPosition));
            }
        });
        diffResult.dispatchUpdatesTo(this);
        // 通知刷新了之后，要更新副本数据到最新
        temp.clear();
        temp.addAll(datas);
    }
}
