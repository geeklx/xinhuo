package com.bokecc.livemodule.replay.doc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.utils.TimeUtil;
import com.bokecc.sdk.mobile.live.replay.pojo.ReplayPageInfo;
import com.bumptech.glide.Glide;

import java.util.List;

public class ReplayPageInfoListAdapter extends RecyclerView.Adapter<ReplayPageInfoListAdapter.PageViewHolder> {
    private Context mContext;
    private List<ReplayPageInfo> infoList;
    private LayoutInflater mInflater;
    private OnClick onClick;

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    public void setSelectItem(int selectItem) {
        if (infoList != null && selectItem < infoList.size()) {
            this.selectItem = selectItem;
            notifyDataSetChanged();
        }
    }

    private int selectItem = 0;

    public ReplayPageInfoListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setInfoList(List<ReplayPageInfo> infoList) {
        this.infoList = infoList;
        notifyDataSetChanged();
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.page_info_item_layout, parent, false);
        return new PageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PageViewHolder holder, int position) {
        final ReplayPageInfo pageInfo = infoList.get(position);
        Glide.with(mContext).load(pageInfo.getUrl()).into(holder.preview);
//        holder.page.setText(pageInfo.getDocName());
//        holder.course.setText(pageInfo.getPageTitle());//
//
        holder.page.setText("第" + (position + 1) + "页");
        holder.course.setText(pageInfo.getDocName());

        if (selectItem == position) {
            holder.section_time.setBackgroundResource(R.drawable.current_section_bg);
            holder.section_time.setText("播放中");
            holder.section_time.setTextColor(Color.WHITE);
            holder.itemView.setSelected(true);
            holder.itemView.setBackgroundResource(android.R.color.white);
        } else {
            holder.section_time.setBackground(null);
            holder.section_time.setText(TimeUtil.displayHHMMSS(pageInfo.getTime()));
            holder.section_time.setTextColor(Color.parseColor("#8a95a1"));
            holder.itemView.setSelected(false);
            holder.itemView.setBackgroundResource(android.R.color.transparent);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem = holder.getAdapterPosition();
                onClick.OnItemClick(pageInfo.getTime() * 1000L);
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return infoList == null ? 0 : infoList.size();
    }

    final class PageViewHolder extends RecyclerView.ViewHolder {

        ImageView preview;
        TextView page;
        TextView course;
        TextView section_time;

        PageViewHolder(View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.preview);
            page = itemView.findViewById(R.id.page);
            course = itemView.findViewById(R.id.course);
            section_time = itemView.findViewById(R.id.section_time);
        }
    }

    public interface OnClick {
        void OnItemClick(long time);
    }
}
