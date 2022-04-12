package io.ketill;

import org.jetbrains.annotations.NotNull;

final class StateContainer<I, U> {

    final @NotNull I internal;
    final @NotNull U container;

    StateContainer(@NotNull I internal, @NotNull U user) {
        this.container = user;
        this.internal = internal;
    }

}
