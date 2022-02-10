package io.ketill.controller;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

public class AnalogTrigger extends IoFeature<Trigger1f> {

    public AnalogTrigger(@NotNull String id) {
        super(id, Trigger1f::new);
    }

}
