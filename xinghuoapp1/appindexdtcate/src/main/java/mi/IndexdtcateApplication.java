package mi;

import com.geek.libbase.AndroidApplication;
import com.geek.libutils.app.AppUtils;

public class IndexdtcateApplication extends AndroidApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!AppUtils.isProcessAs(getPackageName(), this)) {
            return;
        }
        //TODO commonbufen
        configBugly("测试", "3aeeb18e5e");
        configHios();
        configmmkv();
        configShipei();
        configRetrofitNet();
        others();
        //TODO 业务bufen

    
    }

    @Override
    public void onHomePressed() {
        super.onHomePressed();
//        AddressSaver.addr = null;
    }
}
