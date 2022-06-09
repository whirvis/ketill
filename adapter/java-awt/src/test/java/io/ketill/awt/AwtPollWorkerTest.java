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
    void testIsClosed() {
        assertFalse(worker.isClosed());
        worker.close();
        assertTrue(worker.isClosed());
    }

    @Test
    void testClose() throws InterruptedException {
        worker.close();

        /*
         * Once a worker has been closed, it should no longer be polled
         * by the background thread. If this occurs, then it means that
         * the worker closing is broken.
         */
        reset(device); /* clear previous invocations */
        Thread.sleep(100); /* wait for worker */
        verify(device, never()).poll();

        /*
         * Once a worker has been close, it can be closed again without
         * problem. It just shouldn't do anything. This is to comply with
         * Java's Closeable interface.
         */
        assertDoesNotThrow(() -> worker.close());
    }

    @Test
    void ensureImplementsToString() {
        assertImplementsToString(AwtPollWorker.class, worker);
    }

    @AfterEach
    void closeWorker() {
        /* prevent lingering background thread */
        worker.close();
    }

}
