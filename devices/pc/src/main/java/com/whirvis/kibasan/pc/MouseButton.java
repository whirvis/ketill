package com.whirvis.kibasan.pc;

import com.whirvis.kibasan.Button1b;
import com.whirvis.kibasan.DeviceFeature;
import org.jetbrains.annotations.NotNull;

public class MouseButton extends DeviceFeature<Button1b> {

    public MouseButton(@NotNull String id) {
        super(id, Button1b::new);
    }

}
