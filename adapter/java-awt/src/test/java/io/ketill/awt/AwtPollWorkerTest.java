package io.ketill.awt;

import io.ketill.IoDevice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AwtPollWorkerTest {

    private IoDevice device;
    private AwtPollWorker<IoDevice> worker;

    @BeforeEach
    void createDevice() {
        this.device = mock(IoDevice.class);
        this.worker = AwtPollWorker.pollInBackground(device);
    }

    @Test
    void testPollInBackground() throws InterruptedException {
        /*
         * After 100ms, the poll worker should have polled the device at
         * least once. If it takes longer than 100ms, then the computer is
         * either exceptionally slow or the worker is broken.
         */
        Thread.sleep(100); /* wait for worker */
        verify(device, atLeastOnce()).poll();
    }

    @Test
    void testGetDevice() {
        assertSame(device, worker.getDevice());
    }

    @Test
    void testIsClosed() {
        assertFalse(worker.isClosed());
        worker.close();
        assertTrue(worker.isClosed());
    }

    @Test
    void testClose() throws InterruptedException {
        /*
         * Once a worker has been closed, it should no longer be polled
         * by the background thread. If this occurs, then it means that
         * the worker closing is broken.
         *
         * Take note that a sleep is used here to ensure that poll() is
         * not called by the background thread that close() should have
         * caused to stop running.
         */
        worker.close(); /* trigger thread shutdown */
        reset(device); /* clear previous invocations */
        Thread.sleep(100); /* wait for worker */
        verify(device, never()).poll();

        /*
         * Once a worker has been closed, it can be closed again without
         * problem. It just shouldn't do anything. This is to comply with
         * Java's Closeable interface.
         */
        assertDoesNotThrow(() -> worker.close());
    }

    @Test
    void testCloseInterrupt() {
        /*
         * It is possible the calling thread will be interrupted while
         * shutting down the poll thread. If this occurs, it should just
         * be propagated to the current thread.
         */
        AwtPollWorker.interruptQuitPolling = true;
        worker.close(); /* trigger thread shutdown */
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void ensureImplementsToString() {
        assertImplementsToString(AwtPollWorker.class, worker);
    }

    @AfterEach
    void closeWorker() {
        worker.close(); /* prevent lingering thread */
    }

}
