package bz.infectd.crypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.crypto.SecretKey;

import org.junit.Test;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class SecretKeyUtilTest {

    @Test
    public void createsKey() {
        SecretKey key = SecretKeyUtils.generate();
        assertEquals("AES", key.getAlgorithm());
    }

    @Test
    public void createsRandomKey() {
        SecretKey key1 = SecretKeyUtils.generate();
        SecretKey key2 = SecretKeyUtils.generate();
        assertNotEquals(key1, key2);
    }

    @SuppressWarnings("resource")
    @Test
    public void writesToFileAndLoads() throws IOException {
        SecretKey key1 = SecretKeyUtils.generate();
        File tmpFile = File.createTempFile("test", "secret");
        FileChannel chn = new FileOutputStream(tmpFile).getChannel();
        SecretKeyUtils.writeKey(key1, chn);
        chn.close();
        chn = new FileInputStream(tmpFile).getChannel();
        SecretKey key2 = SecretKeyUtils.loadKey(chn, (int) chn.size());
        assertEquals(key1, key2);
        chn.close();
    }

}
