package tuoyan.com.xinghuo_dayingindex.utlis;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.AppUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.udesk.UdeskSDKManager;
import cn.udesk.config.UdeskConfig;
import tuoyan.com.xinghuo_dayingindex.BuildConfig;
import udesk.core.LocalManageUtil;
import udesk.core.UdeskConst;
import udesk.core.model.Product;

public class UdeskUtils {

    public static void openChatView(Context context, Product product) {
        LocalManageUtil.saveSelectLanguage(context, Locale.CHINA);
        Map<String, String> info = new HashMap<String, String>();
        String token = SpUtil.INSTANCE.getUser().getToken();
        String userid = SpUtil.INSTANCE.getUser().getUserId();
        String phone = SpUtil.INSTANCE.getUserInfo().getPhone();
        info.put(UdeskConst.UdeskUserInfo.USER_SDK_TOKEN, userid);
        info.put(UdeskConst.UdeskUserInfo.NICK_NAME, phone);
        info.put(UdeskConst.UdeskUserInfo.DESCRIPTION, "当前App版本号：" + AppUtils.getAppVersionName());
        UdeskConfig.Builder builder = new UdeskConfig.Builder();
        if (product != null) {
            builder.setProduct(product).setUdeskProductRightNameLinkColorResId(android.R.color.white);
        }
        builder.setDefaultUserInfo(info);
        builder.setTxtMessageClick(url -> {
            goDetail(context, url);
        }).setProductMessageClick(url -> {
            goDetail(context, url);
        });
        UdeskSDKManager.getInstance().entryChat(context, builder.build(), userid);
    }

    public static void openChatView(Context context) {
        openChatView(context, null);
    }

    private static void goDetail(Context context, String url) {
        try {
            URL tempUrl = new URL(url);
            String path = tempUrl.getPath();
            String query = tempUrl.getQuery();
            if (path.contains("network/networkDetails") || path.contains("network/network")) {
                Map<String, String> paramsMap = new HashMap<>();
                String[] params = query.split("&");
                for (String item : params) {
                    String[] par = item.split("=");
                    paramsMap.put(par[0], par[1]);
                }
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("tuoyan.com.xinghuo_daying", "tuoyan.com.xinghuo_daying.ui.netLesson.lesson.detail.LessonDetailActivity"));
                intent.putExtra("key", paramsMap.get("key"));
                context.startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("tuoyan.com.xinghuo_daying", "tuoyan.com.xinghuo_daying.ui.bookstore.post.PostActivity"));
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
