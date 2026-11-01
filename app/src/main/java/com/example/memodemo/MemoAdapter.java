package com.example.memodemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MemoAdapter extends BaseAdapter {

    /** 条目点击 / 长按回调：卡片自身可点击（带涟漪），故不依赖 ListView 的 ItemClickListener */
    public interface OnMemoActionListener {
        void onClick(Memo memo);

        void onLongClick(Memo memo);
    }

    private Context ctx;
    private List<Memo> memoList;
    private OnMemoActionListener actionListener;

    public MemoAdapter(Context ctx, List<Memo> memoList) {
        this.ctx = ctx;
        this.memoList = memoList;
    }

    public void setData(List<Memo> newList) {
        this.memoList = newList;
        notifyDataSetChanged();
    }

    public void setOnMemoActionListener(OnMemoActionListener listener) {
        this.actionListener = listener;
    }

    @Override
    public int getCount() {
        return memoList.size();
    }

    @Override
    public Object getItem(int i) {
        return memoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return memoList.get(i).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_memo, parent, false);
            holder = new ViewHolder();
            holder.tvTitle = convertView.findViewById(R.id.tv_title);
            holder.tvContent = convertView.findViewById(R.id.tv_content);
            holder.ivStar = convertView.findViewById(R.id.iv_star);
            holder.viewAccent = convertView.findViewById(R.id.view_accent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Memo memo = memoList.get(position);

        // 标题
        String title = memo.getTitle();
        holder.tvTitle.setText(title == null || title.trim().isEmpty()
                ? ctx.getString(R.string.untitled) : title);

        // 摘要：无内容时给一个浅提示
        String content = memo.getContent();
        if (content == null || content.trim().isEmpty()) {
            holder.tvContent.setText(R.string.no_content);
            holder.tvContent.setAlpha(0.65f);
        } else {
            holder.tvContent.setText(content.trim());
            holder.tvContent.setAlpha(1f);
        }

        // 重要备忘录：显示星标 + 左侧强调条
        int importantVisible = memo.isImportant() ? View.VISIBLE : View.GONE;
        holder.ivStar.setVisibility(importantVisible);
        holder.viewAccent.setVisibility(importantVisible);

        // 点击编辑、长按删除
        convertView.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onClick(memo);
            }
        });
        convertView.setOnLongClickListener(v -> {
            if (actionListener != null) {
                actionListener.onLongClick(memo);
            }
            return true;
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle, tvContent;
        ImageView ivStar;
        View viewAccent;
    }
}
