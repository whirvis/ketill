package com.whirvis.ketill.psx;

import com.whirvis.ketill.DeviceFeature;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

public class Lightbar extends DeviceFeature<Vector4f> {

    public Lightbar(@NotNull String id) {
        super(id, Vector4f::new);
    }

}
