package com.spark.peak.ui.video.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spark.peak.bean.Lyric;

import java.util.ArrayList;
import java.util.List;


public class LyricAdapter extends BaseAdapter {

    public List<Lyric> mLyrics = null;
    private Context mContext = null;

    private int mIndexOfCurrentSentence = 6;
    private float mCurrentSize = 20;
    private float mNotCurrentSize = 17;

    public LyricAdapter(Context context) {
        mContext = context;
        mLyrics = new ArrayList<>();
        mIndexOfCurrentSentence = 7;
    }


    public void setLyric(List<Lyric> lyric) {
        mLyrics.clear();
        if (lyric != null) {
            mLyrics.addAll(lyric);
        }
        for (int a = 0; a < 7; a++) mLyrics.add(0, new Lyric(0, ""));
        for (int a = 0; a < 9; a++) mLyrics.add(mLyrics.size(), new Lyric(0, ""));

        mIndexOfCurrentSentence = 7;
    }

    @Override
    public boolean isEmpty() {
        // 歌词为空时，让ListView显示EmptyView
        if (mLyrics == null) {
            return true;
        } else if (mLyrics.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return mLyrics == null ? 0 : mLyrics.size();
    }

    @Override
    public Object getItem(int position) {
        return mLyrics.get(position).lyricText;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(mContext);
        textView.setTextSize(15f);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setLayoutParams(lp);

        if (position >= 0 && position < mLyrics.size()) {
            textView.setText(mLyrics.get(position)
                    .lyricText);
        }
        if (mIndexOfCurrentSentence == position) {
            textView.setTextColor(Color.parseColor("#1482ff"));
            textView.setTextSize(mCurrentSize);
        } else {
            textView.setTextColor(Color.parseColor("#222831"));
            textView.setTextSize(mNotCurrentSize);
        }
        return textView;
    }

    public void setCurrentSentenceIndex(int index) {
        mIndexOfCurrentSentence = index;
    }

}
