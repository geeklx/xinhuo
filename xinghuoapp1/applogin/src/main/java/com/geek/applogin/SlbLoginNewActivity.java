package com.geek.applogin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.geek.appcommon.SlbBase;
import com.geek.applogin.fragment.LoginFragment;
import com.geek.applogin.fragment.RegisterFragment;
import com.geek.biz1.bean.FinitBean;
import com.geek.libutils.data.MmkvUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SlbLoginNewActivity extends SlbBase {

    private String[] titles = {"登录", "注册"};
    private List<Fragment> fragmentList = new ArrayList<>();
    private FinitBean fconfigBean;
    private ImageView iv_back;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_slblogin_new;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        findview();
        donetwork();
    }

    private void donetwork() {
        //
        fconfigBean = MmkvUtils.getInstance().get_common_json("config", FinitBean.class);
        // 强制登录bufen
        if (fconfigBean!=null){
            if (TextUtils.equals("true", fconfigBean.getLogin_login())) {
                iv_back.setVisibility(View.INVISIBLE);
            } else {
                iv_back.setVisibility(View.VISIBLE);
            }
        }
    }


    private void findview() {
        //

        TabLayout tabs = findViewById(R.id.tabs);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        LoginFragment loginFragment = new LoginFragment();
        fragmentList.add(loginFragment);
        RegisterFragment registerFragment = new RegisterFragment();
        registerFragment.setOnRegisterListener(new RegisterFragment.OnRegisterListener() {
            @Override
            public void onUpdate() {
                Objects.requireNonNull(tabs.getTabAt(0)).select();
            }
        });

        fragmentList.add(registerFragment);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getItemCount() {
                return fragmentList.size();
            }
        });

        viewPager.setOffscreenPageLimit(2);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabs, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }
        });
        //这句话很重要
        tabLayoutMediator.attach();

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onLoginCanceled(AppUtils.getAppPackageName() + ".hs.act.slbapp.IndexAct");
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActResult(int requestCode, int resultCode, Intent data) {
        super.onActResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (fconfigBean != null) {
            if (TextUtils.equals("true", fconfigBean.getLogin_login())) {
            // tuichu
                if (ActivityUtils.getActivityList().size() == 1) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                } else {
                    onLoginCanceled(AppUtils.getAppPackageName() + ".hs.act.slbapp.shouye");
                }

            } else {
                // hui index
                onLoginCanceled(AppUtils.getAppPackageName() + ".hs.act.slbapp.shouye");
            }
        } else {
            onLoginCanceled(AppUtils.getAppPackageName() + ".hs.act.slbapp.shouye");
        }
        super.onBackPressed();
    }

}
