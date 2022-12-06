package com.geek.appcommon.video;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geek.common.R;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.dkplayer.adapter.TikTokListAdapterDk;
import xyz.doikki.dkplayer.bean.TiktokBeanDk;
import xyz.doikki.dkplayer.fragment.BaseFragmentDk;
import xyz.doikki.dkplayer.util.DataUtilDk;

public class TikTokListFragmentDk extends BaseFragmentDk {
    private List<TiktokBeanDk> data = new ArrayList();
    private RecyclerView mRecyclerView;
    private TikTokListAdapterDk mAdapter;
    private Button mSwitchImpl;

    public TikTokListFragmentDk() {
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tiktok_listdk;
    }

    @Override
    protected void initView() {
        super.initView();
        this.mRecyclerView = (RecyclerView) this.findViewById(R.id.rv_tiktok);
        this.mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        this.mAdapter = new TikTokListAdapterDk(this.data);
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mSwitchImpl = (Button) this.findViewById(R.id.btn_switch_impl);
        final PopupMenu menu = new PopupMenu(this.getContext(), this.mSwitchImpl);
        menu.inflate(R.menu.tiktok_impl_menu);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                TikTokListFragmentDk.this.mAdapter.setImpl(item.getItemId());
                int itemId = item.getItemId();
                if (itemId == R.id.impl_recycler_view) {
                    TikTokListFragmentDk.this.mSwitchImpl.setText("RecyclerView");
                } else if (itemId == R.id.impl_vertical_view_pager) {
                    TikTokListFragmentDk.this.mSwitchImpl.setText("VerticalViewPager");
                } else if (itemId == R.id.impl_view_pager_2) {
                    TikTokListFragmentDk.this.mSwitchImpl.setText("ViewPager2");
                }

                return false;
            }
        });
        this.mSwitchImpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.show();
            }
        });
        this.mAdapter.setImpl(R.id.impl_vertical_view_pager);
        this.mSwitchImpl.setText("VerticalViewPager");
    }

    @Override
    protected boolean isLazyLoad() {
        return true;
    }

    @Override
    protected void initData() {
        super.initData();
        (new Thread(new Runnable() {
            @Override
            public void run() {
                List<TiktokBeanDk> tiktokBeans = DataUtilDk.getTiktokDataFromAssets(TikTokListFragmentDk.this.getActivity());
                TikTokListFragmentDk.this.data.addAll(tiktokBeans);
                TikTokListFragmentDk.this.mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        TikTokListFragmentDk.this.mAdapter.notifyDataSetChanged();
                    }
                });
            }
        })).start();
    }
}
