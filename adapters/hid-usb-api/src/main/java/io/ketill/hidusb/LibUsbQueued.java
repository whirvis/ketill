package io.ketill.hidusb;

final class LibUsbQueued<L> {

    final L device;
    int attemptsLeft;
    long lastAttempt;

    LibUsbQueued(L device, int attempts) {
        this.device = device;
        this.attemptsLeft = attempts;
    }

}
