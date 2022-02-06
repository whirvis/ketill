package com.whirvis.ketill.pc;

import com.whirvis.ketill.Button1b;
import com.whirvis.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

public class MouseButton extends IoFeature<Button1b> {

    public MouseButton(@NotNull String id) {
        super(id, Button1b::new);
    }

}
