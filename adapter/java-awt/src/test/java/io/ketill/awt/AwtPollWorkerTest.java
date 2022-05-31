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
        Thread.sleep(100); /* wait for worker */
        verify(device, atLeastOnce()).poll();
    }

    @Test
    void testGetDevice() {
        assertSame(device, worker.getDevice());
    }

    @Test
    void testIsCancelled() {
        assertFalse(worker.isCancelled());
        worker.cancel();
        assertTrue(worker.isCancelled());
    }

    @Test
    void testCancel() throws InterruptedException {
        worker.cancel();

        /*
         * Once a worker has been cancelled, it should no longer be polled
         * by the background thread. If this occurs, then it means that the
         * worker cancellation is broken.
         */
        reset(device); /* clear previous invocations */
        Thread.sleep(100); /* wait for worker */
        verify(device, never()).poll();

        /*
         * Once a worker has been cancelled, it can be cancelled again
         * without problem. It just shouldn't do anything.
         */
        assertDoesNotThrow(() -> worker.cancel());
    }

    @Test
    void ensureImplementsToString() {
        assertImplementsToString(AwtPollWorker.class, worker);
    }

    @AfterEach
    void cancelWorker() {
        /* prevent lingering background thread */
        worker.cancel();
    }

}
