package com.whirvis.ketill.pc;

import com.whirvis.ketill.Button1b;
import com.whirvis.ketill.DeviceFeature;
import org.jetbrains.annotations.NotNull;

public class KeyboardKey extends DeviceFeature<Button1b> {

    public KeyboardKey(@NotNull String id) {
        super(id, Button1b::new);
    }

}
