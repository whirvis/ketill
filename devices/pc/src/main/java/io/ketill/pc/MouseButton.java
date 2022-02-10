package io.ketill.pc;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

public class MouseButton extends IoFeature<Click1b> {

    public MouseButton(@NotNull String id) {
        super(id, Click1b::new);
    }

}
