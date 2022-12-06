package tuoyan.com.xinghuo_dayingindex.encrypt;

import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by sdj on 2018/1/2.
 */

public class FileReadUtils {
    public static final String TAG = "FileReadUtils.java";

    public void readHeadData(String path) throws Exception {
        if (TextUtils.isEmpty(path)) return;
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[512];
            int len = 0;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int header = fis.read(buffer);
            if (header != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            outStream.close();
            fis.close();
            Log.e(TAG, new String(data));
        }

    }

    public void readFooterData(String path) throws Exception {
        if (TextUtils.isEmpty(path)) return;
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[512];
            int len = 0;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            while ((len = fis.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            outStream.close();
            fis.close();
            Log.e(TAG, new String(data));
        }
    }

    public void readMiddleData(String path) throws Exception {
        if (TextUtils.isEmpty(path)) return;
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = (int) (file.length() - 512);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            while ((len = fis.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            outStream.close();
            fis.close();
            Log.e(TAG, new String(data));
        }
    }
}
