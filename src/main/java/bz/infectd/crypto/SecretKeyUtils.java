package bz.infectd.crypto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.io.BaseEncoding;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class SecretKeyUtils {

    private static final String CRYPTO_ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    private static KeyGenerator generator;
    private static BaseEncoding base32 = BaseEncoding.base32();

    static {
        try {
            generator = KeyGenerator.getInstance(CRYPTO_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        generator.init(KEY_SIZE, new SecureRandom());
    }

    public static SecretKey generate() {
        return generator.generateKey();
    }

    public static void writeKey(SecretKey key, WritableByteChannel chn) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(base32.encode(key.getEncoded()).getBytes());
        chn.write(buf);
    }

    public static SecretKey loadKey(ReadableByteChannel chn, int keySize) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(keySize);
        chn.read(buf);
        byte[] bytes = base32.decode(new String(buf.array()));
        return new SecretKeySpec(bytes, CRYPTO_ALGORITHM);
    }
}
