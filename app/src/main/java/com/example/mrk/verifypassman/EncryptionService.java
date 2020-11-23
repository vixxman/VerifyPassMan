package com.example.mrk.verifypassman;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EncryptionService {
    static byte[] key = "1234567890123456".getBytes();

    public static String EncryptAES(String plaintext)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return android.util.Base64.encodeToString(cipherText, android.util.Base64.DEFAULT);
    }

    public static String DecryptAES(String cipherText)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedCipherText = android.util.Base64.decode(cipherText, android.util.Base64.DEFAULT);
        byte[] decryptedText = cipher.doFinal(decodedCipherText);

        return new String(decryptedText, StandardCharsets.UTF_8);
    }
}