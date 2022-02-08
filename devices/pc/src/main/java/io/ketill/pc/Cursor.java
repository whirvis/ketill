package io.ketill.pc;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

public class Cursor extends IoFeature<Cursor2f> {

    public Cursor(@NotNull String id) {
        super(id, Cursor2f::new);
    }

}
