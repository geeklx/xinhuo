package com.geek.appindex.addrecycleview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.geek.appcommon.AppCommonUtils;
import com.geek.appcommon.SlbBase;
import com.geek.appindex.R;
import com.geek.biz1.bean.BjyyActFragment251Bean;
import com.geek.biz1.bean.BjyyActFragment251Bean2;
import com.geek.biz1.bean.BjyyBeanYewu1;
import com.geek.biz1.bean.BjyyBeanYewu2;
import com.geek.biz1.bean.BjyyBeanYewu3;
import com.geek.biz1.bean.FgrxxBean;
import com.geek.biz1.presenter.Fsyyy2Presenter;
import com.geek.biz1.presenter.Fsyyy3Presenter;
import com.geek.biz1.presenter.FsyyyPresenter;
import com.geek.biz1.view.Fsyyy2View;
import com.geek.biz1.view.Fsyyy3View;
import com.geek.biz1.view.FsyyyView;
import com.geek.libbase.base.SlbBaseFragment;
import com.geek.libbase.base.SlbBaseLazyFragmentNew;
import com.geek.libutils.app.FragmentHelper;
import com.geek.libutils.app.LocalBroadcastManagers;
import com.geek.libutils.data.MmkvUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BjyyAct extends SlbBase implements OnClickListener, FsyyyView, Fsyyy2View, Fsyyy3View {

    private TextView tv_qx;
    private TextView tv_bc;
    private BjyyBeanYewu1 data;
    private BjyyActFragment251Bean2 data1;
    private FsyyyPresenter fsyyyPresenter;
    private Fsyyy2Presenter fsyyy2Presenter;
    private FgrxxBean fgrxxBean;

    @Override
    protected void onDestroy() {
        if (fsyyyPresenter != null) {
            fsyyyPresenter.onDestory();
        }
        if (fsyyy2Presenter != null) {
            fsyyy2Presenter.onDestory();
        }
        super.onDestroy();
    }

//    public BjyyBeanYewu1 getData() {
//        return data;
//    }
//
//    public void setData(BjyyBeanYewu1 data) {
//        this.data = data;
//    }
//
//    public BjyyActFragment251Bean2 getData1() {
//        return data1;
//    }
//
//    public void setData1(BjyyActFragment251Bean2 data1) {
//        this.data1 = data1;
//    }

    private List<BjyyBeanYewu3> getList1() {
        List<BjyyBeanYewu3> mList1 = new ArrayList<>();
        BjyyBeanYewu3 bean1 = new BjyyBeanYewu3("hios", "https://s2.51cto.com//wyfs02/M01/89/BA/wKioL1ga-u7QnnVnAAAfrCiGnBQ946_middle.jpg", "打开应用详情", AppUtils.getAppPackageName() + ".hs.act.slbapp.ChangeIconActivity", true);
        BjyyBeanYewu3 bean2 = new BjyyBeanYewu3("http", "https://s2.51cto.com//wyfs02/M01/89/BA/wKioL1ga-u7QnnVnAAAfrCiGnBQ946_middle.jpg", "打开微应用", "https://www.baidu.com/", true);
        BjyyBeanYewu3 bean3 = new BjyyBeanYewu3("others", "https://s2.51cto.com//wyfs02/M01/89/BA/wKioL1ga-u7QnnVnAAAfrCiGnBQ946_middle.jpg", "打开微信", "com.tencent.mm", true);
        mList1.add(bean1);
        mList1.add(bean2);
        mList1.add(bean3);
        for (int i = 0; i < 10; i++) {
            BjyyBeanYewu3 bean = new BjyyBeanYewu3(i + "", "https://s2.51cto.com//wyfs02/M01/89/BA/wKioL1ga-u7QnnVnAAAfrCiGnBQ946_middle.jpg", i + "geek", "", true);
            mList1.add(bean);
        }
        return mList1;
    }

    // 接口数据bufen
    public BjyyBeanYewu1 getInitData() {
        BjyyBeanYewu1 data1 = new BjyyBeanYewu1();
        List<BjyyBeanYewu2> data2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BjyyBeanYewu2 data3 = new BjyyBeanYewu2();
            List<BjyyBeanYewu3> data4 = new ArrayList<>();
//            for (int j = 0; j < new Random().nextInt(20); j++) {
            for (int j = 0; j < 8; j++) {
                String aaaaa = new Random().nextInt(100000) + "";
                BjyyBeanYewu3 bean = new BjyyBeanYewu3(aaaaa, "https://s2.51cto.com//wyfs02/M01/89/BA/wKioL1ga-u7QnnVnAAAfrCiGnBQ946_middle.jpg", "geek" + aaaaa, "", false);
                data4.add(bean);
            }
            data3.setId("-1");
            data3.setImg("");
            data3.setName("应用分类" + i);
            data3.setUrl("");
            data3.setEnable(false);
            data3.setData(data4);
            data2.add(data3);
        }
        data1.setData(data2);
        return data1;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bjyyact1;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        findview();
        onclickListener();
        fsyyyPresenter = new FsyyyPresenter();
        fsyyyPresenter.onCreate(this);
//            fsyyyPresenter.getsyyy(AppCommonUtils.auth_url + "/myapp/edit", "", "", "");
        fsyyy2Presenter = new Fsyyy2Presenter();
        fsyyy2Presenter.onCreate(this);
        doNetWork();
    }

    private void doNetWork() {
        // 接口bufen
        fgrxxBean = MmkvUtils.getInstance().get_common_json(AppCommonUtils.userInfo, FgrxxBean.class);
        if (fgrxxBean != null) {
            fsyyy2Presenter.getsyyy2(AppCommonUtils.auth_url, fgrxxBean.getUserId(), fgrxxBean.getIdentityId(), fgrxxBean.getOrgId());
        }
    }

    @Override
    public void onBackPressed() {
        if (!is_click) {
            set_pop();
        } else {
            super.onBackPressed();
        }
    }

    private boolean is_click;
    private Fsyyy3Presenter fsyyy3Presenter;

    private void set_data_save() {
        Intent msgIntent = new Intent();
        msgIntent.setAction("BjyyActFragment1");
        msgIntent.putExtra("query1", 1);
        LocalBroadcastManagers.getInstance(BjyyAct.this).sendBroadcast(msgIntent);
        Intent msgIntent2 = new Intent();
        msgIntent2.setAction("BjyyActFragment2");
        msgIntent2.putExtra("query1", 1);
        LocalBroadcastManagers.getInstance(BjyyAct.this).sendBroadcast(msgIntent2);
//        //接口bufen
////        List<String> currentIds = BjyyUtils.choose_data_my();//
//        BjyyActFragment251Bean2 data_id = MmkvUtils.getInstance().get_common_json("BjyyActFragment1", BjyyActFragment251Bean2.class);
//        List<String> currentIds = new ArrayList<>();
//        for (int i = 0; i < data_id.getData().size(); i++) {
//            currentIds.add(data_id.getData().get(i).getmBean().getId());
//        }
//        fsyyy3Presenter = new Fsyyy3Presenter();
//        fsyyy3Presenter.onCreate(this);
//        StringBuilder ids = new StringBuilder();
//        for (int i = 0; i < currentIds.size(); i++) {
//            ids.append(currentIds.get(i)).append(",");
//        }
//        fsyyy3Presenter.getsyyy3(AppCommonUtils.auth_url, fgrxxBean.getUserId(), ids.substring(0, ids.length() - 1).toString());

    }

    private void set_pop() {
        is_click = true;
        new XPopup.Builder(BjyyAct.this).asConfirm("", "编辑的内容还未保存，是否要保存",
                new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        set_data_save();
//                        onBackPressed();
                    }
                },
                new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        onBackPressed();
                    }
                }
        ).show();
    }

    private void onclickListener() {
        tv_qx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_pop();
            }
        });
        tv_bc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_click = true;
                set_data_save();
//                onBackPressed();
            }
        });
    }

    private void findview() {
        tv_qx = findViewById(R.id.tv_qx);
        tv_bc = findViewById(R.id.tv_bc);
        //
        Intent appLinkIntent = getIntent();
        if (appLinkIntent != null) {
            String appLinkAction = appLinkIntent.getAction();
//                if (appLinkAction != null) {
            Uri appLinkData = appLinkIntent.getData();
            if (appLinkData != null) {
                String aaaa = appLinkData.getQueryParameter("query1");
                String bbbb = appLinkData.getQueryParameter("query2");
                String cccc = appLinkData.getQueryParameter("query3");
//                ToastUtils.showLong("query1->" + aaaa + ",query2->" + bbbb + ",query3->" + cccc);
            }
        }
        //
        if (getIntent() != null) {
            String aaa = getIntent().getStringExtra("query1");
            String bbb = getIntent().getStringExtra("query2");
            String ccc = getIntent().getStringExtra("query3");
//            ToastUtils.showLong("query1->" + aaa + ",query2->" + bbb + ",query3->" + ccc);
        }
//        setupFragments();
    }


    /**
     * 初始化首页fragments
     */
    private void setupFragments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SparseArrayCompat<Class<? extends SlbBaseFragment>> array = BjyyActFragmentFactory.get();
        int size = array.size();
        SlbBaseFragment item;
        for (int i = 0; i < size; i++) {
            item = FragmentHelper.newFragment(array.valueAt(i), null);
            ft.replace(array.keyAt(i), item, item.getClass().getName());
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * fragment间通讯bufen
     *
     * @param value 要传递的值
     * @param tag   要通知的fragment的tag
     */
    public void callFragment(Object value, String... tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;
        for (String item : tag) {
            if (TextUtils.isEmpty(item)) {
                continue;
            }

            fragment = fm.findFragmentByTag(item);
            if (fragment != null && fragment instanceof SlbBaseLazyFragmentNew) {
                ((SlbBaseLazyFragmentNew) fragment).call(value);
            }
        }
    }

    private void setData() {
        data = MmkvUtils.getInstance().get_common_json("BjyyActFragment2", BjyyBeanYewu1.class);
        if (data == null) {
            data = new BjyyBeanYewu1();
            MmkvUtils.getInstance().set_common_json2("BjyyActFragment2", data);
        }
    }

    private void setData1() {
        data1 = MmkvUtils.getInstance().get_common_json("BjyyActFragment1", BjyyActFragment251Bean2.class);
        if (data1 == null) {
            data1 = new BjyyActFragment251Bean2();
            List<BjyyActFragment251Bean> data11 = new ArrayList<>();
            data1.setData(data11);
            MmkvUtils.getInstance().set_common_json2("BjyyActFragment1", data1);
        }
    }


    @Override
    public void OnsyyySuccess(BjyyBeanYewu1 bean) {
        MmkvUtils.getInstance().set_common_json2("BjyyActFragment2", bean);
        setupFragments();
    }

    @Override
    public void OnsyyyNodata(String bean) {
        setData();
    }

    @Override
    public void OnsyyyFail(String msg) {
        setData();
    }

    @Override
    public void Onsyyy2Success(BjyyActFragment251Bean2 bean) {
        MmkvUtils.getInstance().set_common_json2("BjyyActFragment1", bean);
        if (fgrxxBean != null) {
            fsyyyPresenter.getsyyy(
                    AppCommonUtils.auth_url + "/myapp/edit",
                    fgrxxBean.getUserId(), fgrxxBean.getIdentityId(),
                    fgrxxBean.getOrgId(),
                    fgrxxBean.getCityName(),
                    "-1");
        }
    }

    @Override
    public void Onsyyy2Nodata(String bean) {
        setData1();
    }

    @Override
    public void Onsyyy2Fail(String msg) {
        setData1();
    }

    @Override
    public void Onsyyy3Success(String bean) {
        ToastUtils.showLong(bean);
    }

    @Override
    public void Onsyyy3Nodata(String bean) {
        ToastUtils.showLong(bean);
    }

    @Override
    public void Onsyyy3Fail(String msg) {
        ToastUtils.showLong(msg);
    }
}
