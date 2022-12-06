package tuoyan.com.xinghuo_dayingindex.encrypt;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.RandomAccessFile;

import tuoyan.com.xinghuo_dayingindex.ConfigKt;


public class FileEnDecryptManager {
    private static final String TAG = FileEnDecryptManager.class.getSimpleName();
    private static final long MB = 1024 * 1024;
    private long len1, len2, len3, offset1, offset2, offset3;
    private static final String LENGTH = "len";
    private static final String OFFSET = "offset";
    private boolean isEncrypt;
    private static final long LARGE_FILE = 50 * MB;

    private FileEnDecryptManager() {
    }

    private static FileEnDecryptManager instance = null;

    public static FileEnDecryptManager getInstance() {
        synchronized (FileEnDecryptManager.class) {
            if (instance == null)
                instance = new FileEnDecryptManager();
        }
        return instance;
    }

    public void encryptFile(String filePath) {
        try {
            long pos = 0, encPos = 0, length = 0, bufferLength = 0;
            File sourceFile = new File(filePath);
            String fileName = filePath.substring(filePath.lastIndexOf("/"));
            File tempFile = new File(Environment.getExternalStorageDirectory() + fileName + ".tmp");
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
            RandomAccessFile sourceRaf = new RandomAccessFile(sourceFile, "r");
            RandomAccessFile tempRaf = new RandomAccessFile(tempFile, "rw");

            if (sourceFile.length() > 2 * MB) {
                byte[] bufferSize = new byte[512 * 1024];
                bufferLength = bufferSize.length;
                //header(encrypt)
                length = sourceRaf.read(bufferSize);
                byte[] encryptHeader = AESUtils.encrypt(bufferSize);
                tempRaf.write(encryptHeader);
                pos += length;
                encPos += encryptHeader.length;
                len1 = encPos;

                long offset = sourceFile.length() / 2 - bufferLength;
                long beforeLen = offset - pos;
                byte[] tempByte = createByteArray(beforeLen);
                bufferLength = tempByte.length;
                long readSize = beforeLen > bufferLength ? bufferLength : beforeLen;

                //header - middle (non-encrypt)
                tempRaf.seek(encPos);
                Log.e(TAG, "header-middle start filePointer= " + sourceRaf.getFilePointer());
                while (pos < offset && (length = sourceRaf.read(tempByte, 0, (int) readSize)) > 0) {
                    tempRaf.write(tempByte);
                    pos += length;
                    encPos += length;
                    beforeLen = offset - pos;
                    tempRaf.seek(encPos);
                    tempByte = createByteArray(beforeLen);
                    bufferLength = tempByte.length;
                    readSize = beforeLen > bufferLength ? bufferLength : beforeLen;
                    Log.e("header-middle ", length + "" + " getFilePointer= " + sourceRaf.getFilePointer());
                }
                offset2 = encPos;
                //middle(encrypt)
                byte[] byteMiddle = new byte[2 * bufferSize.length];
                length = sourceRaf.read(byteMiddle);
                byte[] encryptMiddle = AESUtils.encrypt(byteMiddle);
                tempRaf.seek(offset2);
                tempRaf.write(encryptMiddle);
                pos += length;
                encPos += encryptMiddle.length;
                len2 = encryptMiddle.length;

                offset = sourceFile.length() - bufferLength;
                beforeLen = offset - pos;
                tempByte = createByteArray(beforeLen);
                bufferLength = tempByte.length;
                readSize = beforeLen > bufferLength ? bufferLength : beforeLen;
                //middle - footer(non-encrypt)
                tempRaf.seek(encPos);
                Log.e(TAG, "middle-footer start filePointer= " + sourceRaf.getFilePointer());
                while (pos < offset && (length = sourceRaf.read(tempByte, 0, (int) readSize)) > 0) {
                    tempRaf.write(tempByte);
                    pos += length;
                    encPos += length;
                    beforeLen = offset - pos;
                    tempRaf.seek(encPos);
                    tempByte = createByteArray(beforeLen);
                    bufferLength = tempByte.length;
                    readSize = beforeLen > bufferLength ? bufferLength : beforeLen;
                    Log.e("middle-footer  ", length + " filePointer= " + sourceRaf.getFilePointer());
                }
                offset3 = encPos;
                //footer(encrypt)
                sourceRaf.read(bufferSize);
                byte[] encryptFooter = AESUtils.encrypt(bufferSize);
                tempRaf.seek(offset3);
                tempRaf.write(encryptFooter);
                encPos += encryptFooter.length;
                len3 = encryptFooter.length;
                JSONObject object = createJsonObject(offset1, len1);
                JSONObject object2 = createJsonObject(offset2, len2);
                JSONObject object3 = createJsonObject(offset3, len3);

                //flag(SSCVT SSCVE)
                byte[] aesStart = "SSCVT".getBytes();
                tempRaf.seek(encPos);
                tempRaf.write(aesStart);
                encPos += aesStart.length;
                tempRaf.seek(encPos);
                String aesStr = "{" + "\"index\":" + "[" + object.toString() + "," + object2.toString() + "," + object3.toString() + "]}";
                byte[] aesEnIndex = AESUtils.encrypt(aesStr.getBytes(), AESUtils.INDEX_KEY);
                tempRaf.write(aesEnIndex);
                encPos += aesEnIndex.length;
                tempRaf.seek(encPos);
                String aesEnd = "[type=0]SSCVE";
                tempRaf.write(aesEnd.getBytes());

            } else {
                //小于2M 全部加密
                byte[] bufferSize = new byte[(int) sourceFile.length()];
                //header(encrypt)
                sourceRaf.read(bufferSize, 0, bufferSize.length);
                byte[] encryptAll = AESUtils.encrypt(bufferSize);
                tempRaf.write(encryptAll);
                encPos += encryptAll.length;
                len1 = encryptAll.length;
                //添加标识
                JSONObject object = createJsonObject(offset1, len1);

                //flag(SSCVT SSCVE)
                byte[] aesStart = "SSCVT".getBytes();
                tempRaf.seek(encPos);
                tempRaf.write(aesStart);
                encPos += aesStart.length;
                tempRaf.seek(encPos);
                String aesStr = "{" + "\"index\":" + "[" + object.toString() + "]}";
                byte[] aesEnIndex = AESUtils.encrypt(aesStr.getBytes(), AESUtils.INDEX_KEY);
                tempRaf.write(aesEnIndex);
                encPos += aesEnIndex.length;
                tempRaf.seek(encPos);
                String aesEnd = "[type=0]SSCVE";
                tempRaf.write(aesEnd.getBytes());

            }
            tempFile.renameTo(sourceFile);
            sourceRaf.close();
            tempRaf.close();
        } catch (Exception e) {
            Log.e("FileEnDecryptManager", Log.getStackTraceString(e));
        }
    }

    private byte[] createByteArray(long byteCount) {
        byte[] bytes = null;
        if (byteCount > LARGE_FILE) {
            long ratio = byteCount / MB;
            if (ratio > 100) {
                bytes = new byte[(int) (20 * MB)];
            } else if (ratio > 10) {
                bytes = new byte[(int) (10 * MB)];
            } else if (ratio > 5) {
                bytes = new byte[(int) (5 * MB)];
            }
            Log.e(TAG, "ratio= " + ratio);
        } else if (byteCount > 5 * MB) {
            bytes = new byte[1024 * 1024];
            Log.e(TAG, "5M byteCount= " + byteCount);
        } else {
            bytes = new byte[512 * 1024];
            Log.e(TAG, "512K byteCount= " + byteCount);
        }
        return bytes;
    }

    private JSONObject createJsonObject(long offset, long length) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(OFFSET, offset);
        jsonObject.put(LENGTH, length);
        return jsonObject;
    }

    public boolean isEncrypt(String filePath) {
        File sourceFile = new File(filePath);
        if (sourceFile.exists()) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(sourceFile, "r");
                byte[] byte5 = new byte[5];
                randomAccessFile.seek(sourceFile.length() - byte5.length);
                randomAccessFile.read(byte5);
                isEncrypt = new String(byte5).equalsIgnoreCase("SSCVE");
                randomAccessFile.close();
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return isEncrypt;
            }
        }
        return isEncrypt;
    }

    public String decryptFile(String filePath) {
        try {
            long pos = 0, dePos = 0, length, bufferLength;
            File sourceFile = new File(filePath);
            String fileName = filePath.substring(filePath.lastIndexOf("/"));
            File tempDir = new File(Environment.getExternalStorageDirectory(), ConfigKt.DOWNLOAD_PATH+ "/temp/");
            if (!tempDir.exists()){
                tempDir.mkdirs();
            }

            File tempFile = new File(tempDir, fileName + ".mp4");
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
            RandomAccessFile sourceRaf = new RandomAccessFile(sourceFile, "r");
            RandomAccessFile tempRaf = new RandomAccessFile(tempFile, "rw");
            JSONArray jsonArray = getIndexJsonArray(sourceRaf, sourceFile);
            if (jsonArray.length() > 1) {
                sourceRaf.seek(offset1);
                byte[] header1 = new byte[(int) len1];
                length = sourceRaf.read(header1, 0, (int) len1);
                //header
                byte[] decryptHeader = AESUtils.decrypt(header1);
                tempRaf.write(decryptHeader);
                pos += length;
                dePos += decryptHeader.length;
                //header - middle
                long beforeLen = offset2 - pos;
                byte[] tempByte = createByteArray(beforeLen);
                bufferLength = tempByte.length;
                long readSize = beforeLen > bufferLength ? bufferLength : beforeLen;
                tempRaf.seek(dePos);
                while (pos < offset2 && (length = sourceRaf.read(tempByte, 0, (int) readSize)) > 0) {
                    tempRaf.write(tempByte);
                    pos += length;
                    dePos += length;
                    beforeLen = offset2 - pos;
                    tempByte = createByteArray(beforeLen);
                    bufferLength = tempByte.length;
                    tempRaf.seek(dePos);
                    readSize = beforeLen > bufferLength ? bufferLength : beforeLen;
                }
                //middle
                byte[] byteMiddle = new byte[(int) (len2)];
                length = sourceRaf.read(byteMiddle);
                byte[] decryptMiddle = AESUtils.decrypt(byteMiddle);
                tempRaf.seek(dePos);
                tempRaf.write(decryptMiddle);
                pos += length;
                dePos += decryptMiddle.length;
                beforeLen = offset3 - pos;
                tempByte = createByteArray(beforeLen);
                readSize = beforeLen > bufferLength ? bufferLength : beforeLen;
                //middle - footer
                tempRaf.seek(dePos);
                while (pos < offset3 && (length = sourceRaf.read(tempByte, 0, (int) readSize)) > 0) {
                    tempRaf.write(tempByte);
                    pos += length;
                    dePos += length;
                    beforeLen = offset3 - pos;
                    tempByte = createByteArray(beforeLen);
                    bufferLength = tempByte.length;
                    tempRaf.seek(dePos);
                    readSize = beforeLen > bufferLength ? bufferLength : beforeLen;
                }
                //footer
                byte[] byte3 = new byte[(int) (len3)];
                sourceRaf.read(byte3);
                byte[] decryptFooter = AESUtils.decrypt(byte3);
                tempRaf.seek(dePos);
                tempRaf.write(decryptFooter);
            } else {
                sourceRaf.seek(offset1);
                byte[] byteAll = new byte[(int) len1];
                sourceRaf.read(byteAll, 0, (int) len1);
                byte[] decryptAll = AESUtils.decrypt(byteAll);
                tempRaf.write(decryptAll);
            }

//            tempFile.renameTo(sourceFile);
            sourceRaf.close();
            tempRaf.close();
            return tempFile.getPath();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
//            return "";
        }
        return null;
    }

    private JSONArray getIndexJsonArray(RandomAccessFile sourceRaf, File sourceFile) throws Exception {
        //5个字节
        byte[] byte5 = new byte[5];
        long aesStart = 0, aesEnd = 0;
        int count = byte5.length;
        sourceRaf.seek(sourceFile.length() - count);
        while (sourceRaf.read(byte5) > 0) {
            String byteStr = new String(byte5);
            if (byteStr.equalsIgnoreCase("SSCVT")) {
                aesStart = sourceRaf.getFilePointer();
                sourceRaf.seek(aesStart);
                break;
            } else if (byteStr.equalsIgnoreCase("SSCVE")) {
                aesEnd = sourceRaf.getFilePointer();
            }
            count += 1;
            sourceRaf.seek(sourceFile.length() - count);
        }
        //13 = aseEnd + [type=0].length
        long indexLength = sourceFile.length() - 13 - aesStart;
        byte[] byteIndex = new byte[(int) indexLength];
        sourceRaf.read(byteIndex);
        String indexDeContent = new String(AESUtils.decrypt(byteIndex, AESUtils.INDEX_KEY));
        Log.e("FileEnDe indexDe= ", indexDeContent);
        JSONObject jsonObject = new JSONObject(indexDeContent);
        JSONArray jsonArray = new JSONArray(jsonObject.getString("index"));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            if (i == 0) {
                //header
                len1 = object.getLong(LENGTH);
                offset1 = object.getLong(OFFSET);
            } else if (i == 1) {
                //middle
                len2 = object.getLong(LENGTH);
                offset2 = object.getLong(OFFSET);
            } else if (i == 2) {
                //footer
                len3 = object.getLong(LENGTH);
                offset3 = object.getLong(OFFSET);
            }
        }
        return jsonArray;
    }
}
