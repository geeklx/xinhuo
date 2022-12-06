package com.bokecc.livemodule.live.chat.util;

import androidx.recyclerview.widget.RecyclerView;

public interface ITouchListener {
    void onClick(RecyclerView.ViewHolder viewHolder);
    void onLongPress(RecyclerView.ViewHolder viewHolder);
    void onTouchDown(RecyclerView.ViewHolder viewHolder);
    void onTouchUp(RecyclerView.ViewHolder viewHolder);
}
