package io.ketill.awt;

import io.ketill.IoDevice;
import org.jetbrains.annotations.TestOnly;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

final class AwtPollThread extends Thread {

    @TestOnly
    boolean interruptLowerCPU = false;

    final AtomicBoolean running;
    final List<IoDevice> devices;

    AwtPollThread() {
        super("Ketill-Java-AWT");
        this.running = new AtomicBoolean();
        this.devices = new CopyOnWriteArrayList<>();
    }

    private void lowerCPU() {
        /* simple hack to keep CPU usage low */
        try {
            if (interruptLowerCPU) {
                throw new InterruptedException();
            }
            Thread.sleep(0, 1);
        } catch (InterruptedException e) {
            running.set(false);
            this.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            /*
             * Previously, this method set the running flag to true just
             * before entering the while loop. However, this would cause
             * the thread to hang if the last device was closed too quickly.
             * This exact reason for this is unknown.
             */
            if (!running.get()) {
                throw new IllegalStateException("not running");
            }

            while (running.get()) {
                for (IoDevice device : devices) {
                    device.poll();
                }
                this.lowerCPU();
            }
        } finally {
            /*
             * This prevents a memory leak in the event this thread
             * is stops running without all devices being removed.
             */
            devices.clear();
        }
    }

}
