package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class IoSecret<T> {

    private final @NotNull String id;
    private final boolean mutable;
    private final @Nullable T fallback;

    public IoSecret(@NotNull String id, boolean mutable, @Nullable T fallback) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.mutable = mutable;
        this.fallback = fallback;
    }

    public IoSecret(@NotNull String id, @Nullable T fallback) {
        this(id, false, fallback);
    }

    public IoSecret(@NotNull String id, boolean mutable) {
        this(id, mutable, null);
    }

    public IoSecret(@NotNull String id) {
        this(id, false, null);
    }

    public @NotNull String getId() {
        return this.id;
    }

    public boolean isMutable() {
        return this.mutable;
    }

    public @Nullable T getFallback() {
        return this.fallback;
    }

}
