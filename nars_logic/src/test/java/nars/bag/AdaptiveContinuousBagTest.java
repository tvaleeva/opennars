package nars.bag;

import nars.bag.impl.CacheBag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author me
 */
public class AdaptiveContinuousBagTest {

    @Test
    public void test() {
        assertEquals(0, CacheBag.decimalize(0, 10));
        assertEquals(1, CacheBag.decimalize(0.1f, 10));
        assertEquals(9, CacheBag.decimalize(0.9f, 10));
        assertEquals(9, CacheBag.decimalize(0.925f, 10));
        assertEquals(10, CacheBag.decimalize(0.975f, 10));
        assertEquals(10, CacheBag.decimalize(1.0f, 10));
        
        
        assertEquals(0, CacheBag.decimalize(0f, 9));
        assertEquals(1, CacheBag.decimalize(0.1f, 9));
        assertEquals(8, CacheBag.decimalize(0.9f, 9));
        assertEquals(9, CacheBag.decimalize(1.0f, 9));
    }

}
