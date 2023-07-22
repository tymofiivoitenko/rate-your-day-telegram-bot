package com.tymofiivoitenko.rateyourdaybot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Component
public class AESUtil {

    private static Cipher cipher;
    private static SecretKeySpec secretKey;
    private static byte[] key;

    public AESUtil(@Value("${bot.aesKey}") String aesKey) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = aesKey.getBytes("UTF-8");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
            this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(final String strToEncrypt) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            log.error("Error while encrypting: " + e);
        }
        return null;
    }

    public String decrypt(final String strToDecrypt) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder()
                    .decode(strToDecrypt)));
        } catch (Exception e) {
            log.error("Error while decrypting: " + e);
        }
        return null;
    }
}
