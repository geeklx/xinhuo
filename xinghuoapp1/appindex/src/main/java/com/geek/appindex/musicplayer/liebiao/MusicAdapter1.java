package com.geek.appindex.musicplayer.liebiao;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.geek.appindex.R;
import com.geek.libglide47.base.GlideImageView;

public class MusicAdapter1 extends BaseQuickAdapter<MusicBean1, BaseViewHolder> {

    private int currentPos = -1;

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

//    // 获取歌曲数量
//    public int getCount() {
//        return getData().size();
//    }
//
//    // 获取歌曲名字
//    public Object getItem(int position) {
//        return musicList.get(position);
//    }
//
//    // 获取歌曲对应的索引
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    public MusicAdapter1() {
        super(R.layout.music_list_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicBean1 item) {
        GlideImageView item_image = helper.itemView.findViewById(R.id.item_image);
        TextView item_tv = helper.itemView.findViewById(R.id.item_tv);
        TextView tv_time = helper.itemView.findViewById(R.id.tv_time);
//        item_image.loadImage("", item.getDrawable());
        item_tv.setText(item.getName());
        tv_time.setText(item.getContent1());
        //  播放当前歌曲时，修改图标
        if (helper.getLayoutPosition() == currentPos) {
            item_tv.setEnabled(false);
            item_image.loadImage("", R.drawable.music_playing);
        } else {
            item_tv.setEnabled(true);
            item_image.loadImage("", R.drawable.music);
        }
        helper.addOnClickListener(R.id.item_tv);
    }


}