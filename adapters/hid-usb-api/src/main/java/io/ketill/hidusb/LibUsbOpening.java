package io.ketill.hidusb;

import org.jetbrains.annotations.NotNull;

final class LibUsbOpening<L> {

    final @NotNull L device;
    int attemptsLeft;

    LibUsbOpening(@NotNull L device, int attempts) {
        this.device = device;
        this.attemptsLeft = attempts;
    }

}
