package bz.infectd.crypto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.crypto.SecretKey;

import org.junit.Test;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class MACUtilsTest {

    @Test
    public void generatesSignatureAndChecks() {
        SecretKey key = SecretKeyUtils.generate();
        byte[] content = new String("Something as content of the message").getBytes();
        byte[] mac = MACUtils.generate(content, key);
        assertTrue(MACUtils.check(content, key, mac));
    }

    @Test
    public void generatesSignatureAndChecksWrong() {
        SecretKey key = SecretKeyUtils.generate();
        byte[] content1 = new String("Something as content of the message").getBytes();
        byte[] mac = MACUtils.generate(content1, key);
        byte[] content2 = new String("Something as content of the messag").getBytes();
        assertFalse(MACUtils.check(content2, key, mac));
    }
}
