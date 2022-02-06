package com.whirvis.ketill.pc;

import com.whirvis.ketill.Button1b;
import com.whirvis.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

public class KeyboardKey extends IoFeature<Button1b> {

    public KeyboardKey(@NotNull String id) {
        super(id, Button1b::new);
    }

}
