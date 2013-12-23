package bz.infectd.crypto;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class MACUtils {

    private static final String MAC_ALGORITHM = "HmacSHA512";

    /**
     * @param content
     * @param key
     * @return
     */
    public static byte[] generate(byte[] content, SecretKey key) {
        try {
            Mac hmac = Mac.getInstance(MAC_ALGORITHM);
            hmac.init(key);
            return hmac.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param content
     * @param key
     * @param mac
     * @return
     */
    public static boolean check(byte[] content, SecretKey key, byte[] hmac) {
        byte[] checkMAC = generate(content, key);
        return MessageDigest.isEqual(hmac, checkMAC);
    }

}
