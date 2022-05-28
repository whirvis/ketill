package io.ketill.hidusb;

import org.jetbrains.annotations.NotNull;

final class LibUsbOpening<L> {

    final @NotNull L device;
    int remainingAttempts;

    LibUsbOpening(@NotNull L device, int attempts) {
        this.device = device;
        this.remainingAttempts = attempts;
    }

}
