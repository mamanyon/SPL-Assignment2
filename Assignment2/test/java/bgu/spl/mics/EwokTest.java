package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EwokTest {
    int serialNumber = 1;
    //Object under test
    Ewok e = new Ewok(serialNumber);

    @Test
    void acquireTest(){
        e.acquire();
        assertFalse(e.getAvailability());
    }

    @Test
    void releaseTest(){
        e.release();
        assertTrue(e.getAvailability());
    }
}
