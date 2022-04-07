package io.ketill.hidusb;

import org.jetbrains.annotations.NotNull;

final class LibUsbQueued<L> {

    final @NotNull L device;
    int attemptsLeft;
    long lastAttempt;

    LibUsbQueued(@NotNull L device, int attempts) {
        this.device = device;
        this.attemptsLeft = attempts;
    }

}
