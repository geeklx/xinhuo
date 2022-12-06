package tuoyan.com.xinghuo_dayingindex.encrypt;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

    //ZrCrNWlMuK1X8HGw block
    public static byte[] ENCRYPT_KEY = new byte[]{90, 114, 67, 114, 78, 87, 108, 77, 117, 75, 49, 88, 56, 72, 71, 119};
    //CMsCCBXFUCXg3xAl index
    public static byte[] INDEX_KEY = new byte[]{67, 77, 115, 67, 67, 66, 88, 70, 85, 67, 88, 103, 51, 120, 65, 108};

    private static final String KEY_ALGORITHM = "AES";
    //默认的加密算法
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";


    public static byte[] initSecretKey() {
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
        kg.init(128);
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    public static Key toKey(byte[] key) {
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }


    public static byte[] encrypt(byte[] data, Key key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    public static byte[] encrypt(byte[] data) throws Exception {
        Key key = toKey(ENCRYPT_KEY);
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    public static byte[] encrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
        Key k = toKey(key);
        return encrypt(data, k, cipherAlgorithm);
    }

    public static byte[] encrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    public static byte[] decrypt(byte[] data) throws Exception {
        Key key = toKey(ENCRYPT_KEY);
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    public static byte[] decrypt(byte[] data, Key key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    public static byte[] decrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
        Key k = toKey(key);
        return decrypt(data, k, cipherAlgorithm);
    }

    public static byte[] decrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static String showByteArray(byte[] data) {
        if (null == data) {
            return null;
        }
        StringBuilder sb = new StringBuilder("{");
        for (byte b : data) {
            sb.append(b).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        byte[] key = initSecretKey();
        System.out.println("key：" + showByteArray(key));
        Key k = toKey(key);
        String data = "dsfasdfasfdsfsdfasdfasfdasfsdfasdfasdfasfasdfsdfasfdasfdasfsdfasfsdfasfsdfasdfafadsfasdfadsfadfasfsfasdfdfafdadfafadsfdsafdasfdsfadfdfdfererereredvcvcbbcvdfdsf";
        System.out.println("加密前数据: string:" + data);
        System.out.println("加密前数据: byte[]:" + showByteArray(data.getBytes()));
        System.out.println();
        byte[] encryptData = encrypt(data.getBytes(), k);
        System.out.println("加密后数据: byte[]:" + showByteArray(encryptData));
        System.out.println();
        byte[] decryptData = decrypt(encryptData, k);
        System.out.println("解密后数据: byte[]:" + showByteArray(decryptData));
        System.out.println("解密后数据: string:" + new String(decryptData));
    }
}