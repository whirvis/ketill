package com.whirvis.ketill.pc;

import com.whirvis.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

public class Cursor extends IoFeature<Cursor2f> {

    public Cursor(@NotNull String id) {
        super(id, Cursor2f::new);
    }

}
