package io.ketill.hidusb;

import org.jetbrains.annotations.NotNull;

enum HidPs4Type {

    USB, BT;

    /* @formatter:off */
    private static final String BT_HEADER_STR =
            "{00001124-0000-1000-8000-00805f9b34fb}";
    /* @formatter:on */

    @NotNull
    static HidPs4Type fromPath(@NotNull String path) {
        String id = path.split("#")[1];
        if (id.startsWith(BT_HEADER_STR)) {
            return BT;
        }
        return USB;
    }

}
