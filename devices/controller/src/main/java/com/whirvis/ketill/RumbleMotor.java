package com.whirvis.ketill;

import org.jetbrains.annotations.NotNull;

public class RumbleMotor extends DeviceFeature<Vibration1f> {

    public RumbleMotor(@NotNull String id) {
        super(id, Vibration1f::new);
    }

}
