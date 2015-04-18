/*
 * Copyright 2015, Jakob Korherr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mascherl.session;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Encryption/Decryption helper for the session storage of Mascherl.
 *
 * @author Jakob Korherr
 */
public class CryptoHelper {

    private static final String AES = "AES";
    private static final String SHA_256 = "SHA-256";

    private final String applicationSecret;
    private final String transformation;

    public CryptoHelper(String applicationSecret, String transformation) {
        this.applicationSecret = applicationSecret;
        this.transformation = transformation;
    }

    public String encryptAES(String value) {
        try {
            SecretKeySpec secretKeySpec = secretKeyWithSha256(AES);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            byte[] initiationVector = cipher.getIV();

            byte[] data = new byte[initiationVector.length + encryptedValue.length];
            System.arraycopy(initiationVector, 0, data, 0, initiationVector.length);
            System.arraycopy(encryptedValue, 0, data, initiationVector.length, encryptedValue.length);

            return new String(Base64.getEncoder().encode(data), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public String decryptAES(String value) {
        try {
            byte[] data = Base64.getDecoder().decode(value);
            SecretKeySpec secretKeySpec = secretKeyWithSha256(AES);
            Cipher cipher = Cipher.getInstance(transformation);
            int blockSize = cipher.getBlockSize();
            byte[] initiationVector = Arrays.copyOfRange(data, 0, blockSize);
            byte[] payload = Arrays.copyOfRange(data, blockSize, data.length);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(initiationVector));
            return new String(cipher.doFinal(payload), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    private SecretKeySpec secretKeyWithSha256(String algorithm) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(SHA_256);
        messageDigest.update(applicationSecret.getBytes(StandardCharsets.UTF_8));
        int maxAllowedKeyLengthBits = Cipher.getMaxAllowedKeyLength(algorithm);
        byte[] raw = Arrays.copyOfRange(messageDigest.digest(), 0, maxAllowedKeyLengthBits / 8);
        return new SecretKeySpec(raw, algorithm);
    }

}
