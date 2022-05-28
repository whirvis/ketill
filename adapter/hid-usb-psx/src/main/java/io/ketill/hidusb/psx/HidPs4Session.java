package io.ketill.hidusb.psx;

import io.ketill.psx.Ps4Controller;
import org.jetbrains.annotations.NotNull;

final class HidPs4Session {

    final @NotNull Ps4Controller controller;
    final @NotNull HidPs4Type type;

    HidPs4Session(@NotNull Ps4Controller controller,
                  @NotNull HidPs4Type type) {
        this.controller = controller;
        this.type = type;
    }

}
