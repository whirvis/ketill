package io.ketill.awt;

import io.ketill.IoDevice;

import java.util.ArrayList;
import java.util.List;

final class AwtPollThread extends Thread {

    /**
     * Package-private field for testing. When {@code true}, an
     * {@code InterruptedException} will be thrown by {@link #lowerCPU()}.
     * This is meant for testing only, and should never be {@code true}
     * in production!
     */
    boolean interruptLowerCPU = false;

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
            this.interrupt();
        }
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            synchronized (devices) {
                for (IoDevice device : devices) {
                    device.poll();
                }
            }
            this.lowerCPU();
        }
    }

}
