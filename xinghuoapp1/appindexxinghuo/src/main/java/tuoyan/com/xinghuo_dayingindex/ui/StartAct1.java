package tuoyan.com.xinghuo_dayingindex.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import tuoyan.com.xinghuo_dayingindex.R;
import tuoyan.com.xinghuo_dayingindex.base.BaseActivity;
import tuoyan.com.xinghuo_dayingindex.bean.Advert;
import tuoyan.com.xinghuo_dayingindex.ui._public.WebViewActivity;
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil;
import tuoyan.com.xinghuo_dayingindex.utlis.UuidUtil;

public class StartAct1 extends BaseActivity {

    private Runnable runnable;
    private boolean clicked;

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_start;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getUuid();
        ImmersionBar.with(this).fullScreen(true).statusBarColor(R.color.transparent).statusBarDarkFont(true).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
        setFullScreen(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void configView() {
        super.configView();

    }

    @Override
    public void handleEvent() {
        super.handleEvent();

    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = new Intent(AppUtils.getAppPackageName() + ".WebViewActivity");
        intent.putExtra(WebViewActivity.Companion.getTITLE(), "1111");
        intent.putExtra(WebViewActivity.Companion.getURL(), "2222");
        startActivity(intent);
    }


    /**
     * 用户唯一标识，读取app缓存和本地缓存是否相同，以app缓存为主更新本地缓存
     */

    public void getUuid() {
        //
        try {
            String localUuid = UuidUtil.Companion.readUuid(StartAct1.this);
            String appUuid = SpUtil.INSTANCE.getUuid();
            if (TextUtils.isEmpty(appUuid)) {
                if (TextUtils.isEmpty(localUuid)) {
                    appUuid = UuidUtil.Companion.getUuid();
                    localUuid = appUuid;
                    SpUtil.INSTANCE.setUuid(appUuid);
                    UuidUtil.Companion.saveUuid(localUuid, StartAct1.this);
                } else {
                    appUuid = localUuid;
                    SpUtil.INSTANCE.setUuid(appUuid);
                }
            } else {
                if (!appUuid.equals(localUuid)) {
                    localUuid = appUuid;
                    UuidUtil.Companion.saveUuid(localUuid, StartAct1.this);
                }
            }
        } catch (Exception e) {
            if (TextUtils.isEmpty(SpUtil.INSTANCE.getUuid())) {
                SpUtil.INSTANCE.setUuid(UuidUtil.Companion.getUuid());
            }
        }
    }

    public final void saBanner(@NotNull Advert item, @NotNull String type) {
        try {
            JSONObject property = new JSONObject();
            property.put("advertisement_id", item.getKey());
            property.put("advertisement_name", item.getTitle());
            property.put("location_of_advertisement", "闪屏广告");
            property.put("advertising_sequence", "1号");
            property.put("types_of_advertisement", type);
            SensorsDataAPI.sharedInstance().track("click_advertisement", property);
        } catch (Exception var4) {
        }

    }
}
