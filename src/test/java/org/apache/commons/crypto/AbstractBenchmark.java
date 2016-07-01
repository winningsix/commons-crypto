package org.apache.commons.crypto;

import java.nio.ByteBuffer;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.cipher.CryptoCipherFactory;
import org.apache.commons.crypto.conf.ConfigurationKeys;
import org.apache.commons.crypto.random.CryptoRandom;
import org.apache.commons.crypto.random.CryptoRandomFactory;
import org.junit.Assert;

public abstract class AbstractBenchmark {

    private static final byte[] KEY = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16 };
    private static final byte[] IV = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };
    private static final SecretKeySpec keySpec = new SecretKeySpec(KEY, "AES");
    private static final IvParameterSpec ivSpec = new IvParameterSpec(IV);
    private static final byte[] BUFFER = new byte[1000];

    public AbstractBenchmark() {
        super();
    }

    protected void random(String cipherClass) throws Exception {
        CryptoRandom random = getRandom(cipherClass);
        random.nextBytes(new byte[1000]);
        random.nextBytes(new byte[1000]);
        random.close();
    }

    protected void encipher(String cipherClass) throws Exception {
        CryptoCipher enCipher = getCipher(cipherClass);
        enCipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        final int bufferSize = 1024;
        ByteBuffer inBuffer = ByteBuffer.allocateDirect(bufferSize);
        ByteBuffer outBuffer = ByteBuffer.allocateDirect(bufferSize);
        inBuffer.put(BUFFER);
        inBuffer.flip();
        enCipher.doFinal(inBuffer, outBuffer);
        enCipher.close();
    }

    protected CryptoRandom getRandom(String className) throws Exception {
        Properties props = new Properties();
        props.setProperty(ConfigurationKeys.SECURE_RANDOM_CLASSES_KEY, className);
        final CryptoRandom cryptoRandom = CryptoRandomFactory.getCryptoRandom(props);
        Assert.assertEquals(className, cryptoRandom.getClass().getCanonicalName());
        return cryptoRandom;
    }

    protected CryptoCipher getCipher(String className) throws Exception {
        Properties properties = new Properties();
        properties.setProperty(ConfigurationKeys.CIPHER_CLASSES_KEY, className);
        CryptoCipher cipher = CryptoCipherFactory.getCryptoCipher("AES/CBC/PKCS5Padding", properties);
        Assert.assertEquals(className, cipher.getClass().getCanonicalName());
        return cipher;
    }

}