//package tuoyan.com.xinghuo_daying.tinker;
//
//import android.os.Handler;
//import android.os.Looper;
//import android.widget.Toast;
//
//import com.tencent.tinker.lib.service.DefaultTinkerResultService;
//import com.tencent.tinker.lib.service.PatchResult;
//import com.tencent.tinker.lib.util.TinkerLog;
//import com.tencent.tinker.lib.util.TinkerServiceInternals;
//import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;
//
//import java.io.File;
//
///**
// * 创建者： 
// * 时间：  2019/1/22.
// */
//
//
///**
// * optional, you can just use DefaultTinkerResultService
// * we can restart process when we are at background or screen off
// * Created by zhangshaowen on 16/4/13.
// */
//public class SampleResultService extends DefaultTinkerResultService {
//    private static final String TAG = "Tinker.SampleResultService";
//
//
//    @Override
//    public void onPatchResult(final PatchResult result) {
//        if (result == null) {
//            TinkerLog.e(TAG, "SampleResultService received null result!!!!");
//            return;
//        }
//        TinkerLog.i(TAG, "SampleResultService receive result: %s", result.toString());
//
//        //first, we want to kill the recover process
//        TinkerServiceInternals.killTinkerPatchServiceProcess(getApplicationContext());
//
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (result.isSuccess) {
//                    /**
//                     * 补丁加载完成 杀死进程
//                     */
//                    // TODO: 2019/2/28 14:25   补丁加载完成 杀死进程
////                    Toast.makeText(getApplicationContext(), "patch success, please restart process", Toast.LENGTH_LONG).show();
////                    ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
////                    android.os.Process.killProcess(android.os.Process.myPid());
//                } else {
////                    Toast.makeText(getApplicationContext(), "patch fail, please check reason", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//        // is success and newPatch, it is nice to delete the raw file, and restart at once
//        // for old patch, you can't delete the patch file
//        if (result.isSuccess) {
//            deleteRawPatchFile(new File(result.rawPatchFilePath));
//
//            //not like TinkerResultService, I want to restart just when I am at background!
//            //if you have not install tinker this moment, you can use TinkerApplicationHelper api
//            if (checkIfNeedKill(result)) {
//                if (Utils.isBackground()) {
//                    TinkerLog.i(TAG, "it is in background, just restart process");
//                    restartProcess();
//                } else {
//                    //we can wait process at background, such as onAppBackground
//                    //or we can restart when the screen off
//                    TinkerLog.i(TAG, "tinker wait screen to restart process");
//                    new Utils.ScreenState(getApplicationContext(), new Utils.ScreenState.IOnScreenOff() {
//                        @Override
//                        public void onScreenOff() {
//                            restartProcess();
//                        }
//                    });
//                }
//            } else {
//                TinkerLog.i(TAG, "I have already install the newly patch version!");
//            }
//        }
//    }
//
//    /**
//     * you can restart your process through service or broadcast
//     */
//    private void restartProcess() {
//        TinkerLog.i(TAG, "app is background now, i can kill quietly");
//        //you can send service or broadcast intent to restart your process
//        android.os.Process.killProcess(android.os.Process.myPid());
//    }
//
//}