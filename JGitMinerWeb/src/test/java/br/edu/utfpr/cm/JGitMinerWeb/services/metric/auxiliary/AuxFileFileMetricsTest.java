package br.edu.utfpr.cm.JGitMinerWeb.services.metric.auxiliary;

import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for AuxFileFileMetricsTest class.
 * 
 * @author Rodrigo Takashi Kuroda
 */
public class AuxFileFileMetricsTest {
    private AuxFileFileMetrics instance;
    
    @Before
    public void setup() {
        instance = new AuxFileFileMetrics("FileA.java", "FileB.java");
    }
    
    @After
    public void tearDown() {
        instance = null;
    }
    
    /**
     * Test of equals method. It is equals if the (filename 1 and filename 2)
     * equal a (filename 1 and filename 2) or (filename2 and filename1)
     */
    @Test
    public void testEqualsTrue() {
        AuxFileFileMetrics equal1 = new AuxFileFileMetrics("FileA.java", "FileB.java");
        AuxFileFileMetrics equal2 = new AuxFileFileMetrics("FileB.java", "FileA.java");
        
        assertTrue(instance.equals(equal1));
        assertTrue(instance.hashCode() == equal1.hashCode());
        assertTrue(instance.equals(equal2));
        assertTrue(instance.hashCode() == equal2.hashCode());
    }
    
    /**
     * Test of equals method. It is not equals if the (filename 1 and filename 2)
     * not equal a (filename 1 and filename 2) and (filename2 and filename1)
     */
    @Test
    public void testEqualsFalse() {
        AuxFileFileMetrics notEqual = new AuxFileFileMetrics("FileB.java", "FileC.java");
        assertFalse(instance.equals(notEqual));
        assertFalse(instance.hashCode() == notEqual.hashCode());
    }
}
