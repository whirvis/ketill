package io.ketill.controller;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

public class RumbleMotor extends IoFeature<Vibration1f> {

    public RumbleMotor(@NotNull String id) {
        super(id, Vibration1f::new);
    }

}
