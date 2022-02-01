package com.whirvis.ketill.pc;

import com.whirvis.ketill.Button1b;
import com.whirvis.ketill.DeviceFeature;
import org.jetbrains.annotations.NotNull;

public class MouseButton extends DeviceFeature<Button1b> {

    public MouseButton(@NotNull String id) {
        super(id, Button1b::new);
    }

}
