package tuoyan.com.xinghuo_dayingindex.utlis;

import com.bokecc.sdk.mobile.live.util.SupZipTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 解压缩核心类
 */
public class UnZiper {

    public interface UnZipListener {

        /**
         * 解压失败
         *
         * @param errorCode 错误码
         * @param message   错误内容
         */
        void onError(int errorCode, String message);

        /**
         * 解压完成
         */
        void onUnZipFinish();
    }


    public static int ZIP_WAIT = 10;
    public static int ZIP_ING = 11;
    public static int ZIP_FINISH = 12;
    public static int ZIP_ERROR = 13;

    Thread unzipThread;
    UnZipListener listener;
    File oriFile;
    String dir;

    int status = ZIP_WAIT;

    /**
     * 构造函数
     *
     * @param listener 监听器
     * @param oriFile  解压原始文件
     * @param dir      解压到的文件夹
     */
    public UnZiper(UnZipListener listener, File oriFile, String dir) {
        this.listener = listener;
        this.oriFile = oriFile;
        this.dir = dir;
    }

    /**
     * 构造函数
     *
     * @param listener 监听器
     */
    public UnZiper(UnZipListener listener) {
        this.listener = listener;
    }

    /**
     * 开始解压
     */
    public void unZipFile() {
        if (unzipThread != null && unzipThread.isAlive()) {
            return;
        } else {
            unzipThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    status = ZIP_ING;
                    // 调用解压方法
                    int resultCode = SupZipTool.decompressZipDecAndSplitFile(oriFile.getAbsolutePath(), dir);

                    if (resultCode != 0) {
                        status = ZIP_ERROR;
                        if (listener != null) {
                            listener.onError(resultCode, SupZipTool.getResultMessage(resultCode));
                        }
                    } else {
//                        oriFile.delete();
                        status = ZIP_FINISH;
                        if (listener != null) {
                            listener.onUnZipFinish();
                        }
                    }
                }
            });
            unzipThread.start();
        }
    }

    /**
     * 获取解压状态
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置解压状态
     *
     * @param status
     * @return
     */
    public UnZiper setStatus(int status) {
        this.status = status;
        return this;
    }

    private void unzipFile(String zipPath, String outputPath) throws IOException {
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        InputStream inputStream = new FileInputStream(zipPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        ZipEntry zipEntry = zipInputStream.getNextEntry();
        byte[] buffer = new byte[1024 * 1024];

        int count = 0;
        while (zipEntry != null) {
            if (!zipEntry.isDirectory()) {
                String fileName = zipEntry.getName();
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                file = new File(outputPath + File.separator + fileName);
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                while ((count = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.close();
            }
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    public void startUnzip(String zipPath, String outputPath) {
        if (unzipThread != null && unzipThread.isAlive()) {
            return;
        } else {
            unzipThread = new Thread(() -> {
                try {
                    unzipFile(zipPath, outputPath);
                    if (listener != null) {
                        listener.onUnZipFinish();
                    }
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onError(0, e.getMessage());
                    }
                    e.printStackTrace();
                }
            });
            unzipThread.start();
        }
    }
}
