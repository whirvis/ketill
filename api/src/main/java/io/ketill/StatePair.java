package io.ketill;

import org.jetbrains.annotations.NotNull;

final class StatePair<Z, S> {

    final @NotNull Z internal;
    final @NotNull S container;

    StatePair(@NotNull Z internal, @NotNull S container) {
        this.container = container;
        this.internal = internal;
    }

}
