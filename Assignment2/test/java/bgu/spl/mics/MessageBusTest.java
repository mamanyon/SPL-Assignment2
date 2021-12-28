package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {

    //Objects under test
    private final MessageBus bus = MessageBusImpl.getInstance();
    MicroService m1 = new TestMicroService("first");
    MicroService m2 = new TestMicroService("second");

    @Test
    void testSendBroadcast() {
        try {
            bus.register(m1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Broadcast test = new Broadcast() {};
        m1.sendBroadcast(test);

        try {
            assertEquals(bus.awaitMessage(m1), test);
            assertEquals(bus.awaitMessage(m2), test);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testSendEvent() {
        Event<Boolean> test1 = new TestEventer();

        try {
            bus.register(m2);
            bus.subscribeEvent(TestEventer.class, m2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        m1.sendEvent(test1);
        Message test2 = null;

        try {
            test2 = bus.awaitMessage(m2);
            assertEquals(test2, test1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Test
    void testAwaitMessage() throws InterruptedException {
        bus.register(m1);

        Message test = bus.awaitMessage(m1);
        assertNotEquals(test,null);
    }

    @Test
    void complete() {
        Event<Boolean> test = new TestEventer();

        try {
            bus.register(m2);
            bus.subscribeEvent(TestEventer.class, m2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Future<Boolean> boolFuture = m1.sendEvent(test);

        assert boolFuture != null;
        m2.complete(test, true);
        boolFuture.resolve(true);
        assertTrue(boolFuture.get());

    }
}