package com.whirvis.ketill.pc;

import com.whirvis.ketill.DeviceFeature;
import org.jetbrains.annotations.NotNull;

public class Cursor extends DeviceFeature<Cursor2f> {

    public Cursor(@NotNull String id) {
        super(id, Cursor2f::new);
    }

}
