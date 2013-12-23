package bz.infectd.crypto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class SecretKeyUtils {

    private static final String CRYPTO_ALGORITHM = "AES";

    public static SecretKey generate() {
        try {
            return KeyGenerator.getInstance(CRYPTO_ALGORITHM).generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeKey(SecretKey key, WritableByteChannel chn) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(key.getEncoded());
        chn.write(buf);
    }

    public static SecretKey loadKey(ReadableByteChannel chn, int keySize) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(keySize);
        chn.read(buf);
        return new SecretKeySpec(buf.array(), CRYPTO_ALGORITHM);
    }
}
