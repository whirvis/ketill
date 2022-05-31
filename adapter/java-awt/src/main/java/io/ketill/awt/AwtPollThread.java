package io.ketill.awt;

import io.ketill.IoDevice;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

final class AwtPollThread extends Thread {

    @TestOnly
    boolean interruptLowerCPU = false;

    volatile boolean running;
    final List<IoDevice> devices;

    AwtPollThread() {
        super("Ketill-Java-AWT");
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
            this.running = false;
            this.interrupt();
        }
    }

    @Override
    public void run() {
        this.running = true;
        while (this.running) {
            synchronized (devices) {
                for (IoDevice device : devices) {
                    device.poll();
                }
            }
            this.lowerCPU();
        }
    }

}
