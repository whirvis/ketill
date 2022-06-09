package io.ketill.awt;

import io.ketill.IoDevice;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

final class AwtPollThread extends Thread {

    @TestOnly
    boolean interruptLowerCPU = false;

    final AtomicBoolean running;
    final List<IoDevice> devices;

    AwtPollThread() {
        super("Ketill-Java-AWT");
        this.running = new AtomicBoolean();
        this.devices = new ArrayList<>();
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
        running.set(true);
        while (running.get()) {
            synchronized (devices) {
                for (IoDevice device : devices) {
                    device.poll();
                }
            }
            this.lowerCPU();
        }
    }

}
