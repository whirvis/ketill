package io.ketill.awt;

import io.ketill.IoDevice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AwtPollThreadTest {

    private AwtPollThread thread;

    @BeforeEach
    void createThread() {
        this.thread = new AwtPollThread();
    }

    @Test
    void testRun() throws InterruptedException {
        /*
         * If the running field is not set to be true, this method should
         * throw an IllegalStateException. The user is expected to set the
         * value themselves before starting the thread. This is to prevent
         * the thread from possibly hanging.
         *
         * Take note that run() instead of start() is used here, as using
         * start() would spawn a new Thread (which would throw an exception
         * that JUnit could not catch). All other tests should use start().
         */
        assertThrows(IllegalStateException.class, thread::run);
        thread.running.set(true);

        /* create device to be polled */
        IoDevice device = mock(IoDevice.class);
        thread.devices.add(device);

        /*
         * After 100ms, the poll thread should have polled the device at
         * least once. If it takes longer than 100ms, then the computer is
         * either exceptionally slow or the thread is broken.
         */
        thread.start();
        Thread.sleep(100); /* wait for thread */
        verify(device, atLeastOnce()).poll();

        /*
         * The code below signals an InterruptedException should be thrown
         * by lowerCPU(). This is done to ensure thread interruption works
         * as expected.
         */
        thread.interruptLowerCPU = true;
        Thread.sleep(100); /* wait for thread */
        assertFalse(thread.running.get());

        /*
         * Even though we did not remove the device, the thread stopped
         * due to it being interrupted. To prevent possible memory leaks,
         * the thread should have cleared the devices list itself.
         */
        assertTrue(thread.devices.isEmpty());
    }

    @AfterEach
    void stopThread() {
        thread.running.set(false);
    }

}
