package io.ketill.pc;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

public class KeyboardKey extends IoFeature<Key1b> {

    public KeyboardKey(@NotNull String id) {
        super(id, Key1b::new);
    }

}
