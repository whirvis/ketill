package io.ketill;

import org.jetbrains.annotations.NotNull;

public class AnalogTrigger extends IoFeature<Trigger1f> {

    public AnalogTrigger(@NotNull String id) {
        super(id, Trigger1f::new);
    }

}
