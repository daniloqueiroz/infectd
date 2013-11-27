package bz.infectd;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class ConfigurationTest {

    private Configuration conf;

    @Before
    public void setUp() {
        this.conf = Configuration.getConfiguration();
    }

    @Test
    public void getsDefaultPort() {
        assertEquals(8212, this.conf.networkPort());
    }

    @Test
    public void getsDefaultInterval() {
        assertEquals(20, this.conf.clockInterval());
    }

    @Test
    public void getsDefaultThreads() {
        assertEquals(1, this.conf.threadsCount());
    }

    @Test
    public void getsDefaultRoundsCount() {
        assertEquals(4, this.conf.roundsCount());
    }

    @Test
    public void getsDefaultDebugMode() {
        assertEquals(true, this.conf.debugMode());
    }

    @Test
    public void getsDefaultMinPropagationFactor() {
        assertEquals(3, this.conf.minimunPropagationFactor());
    }

    @Test
    public void getsDefaultPropagationFactor() {
        assertEquals(0.3, this.conf.propagationFactor(), 0.01);
    }

    @Test
    public void getsHostname() {
        assertNotNull(this.conf.hostname());
    }

    @Test
    public void setsHostname() {
        String hostname1 = this.conf.hostname();
        this.conf.hostname(hostname1 + "la");
        String hostname2 = this.conf.hostname();
        assertNotEquals(hostname1, hostname2);
    }
}
