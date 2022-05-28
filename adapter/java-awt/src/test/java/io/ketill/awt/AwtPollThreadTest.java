package io.ketill.awt;

import io.ketill.IoDevice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AwtPollThreadTest {

    private AwtPollThread thread;
    private IoDevice device;

    @BeforeEach
    void startThread() {
        this.thread = new AwtPollThread();
        this.device = mock(IoDevice.class);
        thread.devices.add(device);

        thread.start();
    }

    @Test
    void testRun() throws InterruptedException {
        /*
         * After 100ms, the poll thread should have polled the device at
         * least once. If it takes longer than 100ms, then the computer is
         * either exceptionally slow or the thread is broken.
         */
        Thread.sleep(100); /* wait for thread */
        verify(device, atLeastOnce()).poll();

        /*
         * The code below signals an InterruptedException should be thrown
         * by lowerCPU(). This is done to ensure thread interruption works
         * as expected.
         */
        thread.interruptLowerCPU = true;
        Thread.sleep(100); /* wait for thread */
        assertTrue(thread.isInterrupted());
    }

    @AfterEach
    void interruptThread() {
        thread.interrupt();
    }

}
