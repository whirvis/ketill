package com.whirvis.kibasan.pc;

import com.whirvis.kibasan.DeviceFeature;
import org.jetbrains.annotations.NotNull;

public class Cursor extends DeviceFeature<Cursor2f> {

    public Cursor(@NotNull String id) {
        super(id, Cursor2f::new);
    }

}
