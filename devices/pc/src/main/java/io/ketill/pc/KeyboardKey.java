package io.ketill.pc;

import io.ketill.Button1b;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

public class KeyboardKey extends IoFeature<Button1b> {

    public KeyboardKey(@NotNull String id) {
        super(id, Button1b::new);
    }

}
