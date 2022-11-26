package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;
import java.util.*;

public abstract class IoAdapter<D extends IoDevice> {

    /**
     * When present, signals that a method is an adapter for one or more
     * features. It is <b>required</b> the access level of these methods
     * be {@code private}.
     *
     * @see LinkShorthand
     */
    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    protected @interface ForFeature {
        /* this annotation has no attributes */
    }

    /**
     * When present, signals that a method is a shorthand for linking
     * a feature to an adapter method. It is <b>required</b> the access
     * level of these methods be {@code protected}.
     *
     * @see ForFeature
     */
    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    protected @interface LinkShorthand {
        /* this annotation has no attributes */
    }

    /**
     * An adapter method for an {@link IoFeature}.
     *
     * @see #linkFeature(IoFeature, Object, WithFlow.WithParams)
     */
    protected static final class IoLink {

        static final Object EMPTY_PARAMS = new Object();

        /**
         * An {@code IoLink} which accepts the current {@link IoFlow} of
         * an {@link IoState} as the first parameter.
         *
         * @see WithoutFlow
         */
        protected static final class WithFlow {

            /**
             * An {@code IoLink} with parameters.
             *
             * @param <I> the I/O state's internal type.
             * @param <P> the I/O link's parameter type.
             * @see WithoutParams
             */
            @FunctionalInterface
            public interface WithParams<I, P> {

                /**
                 * Invoked by {@link IoAdapter} as necessary.
                 *
                 * @param flow      the current flow.
                 * @param internals the I/O state internals.
                 * @param params    the link parameters.
                 */
                void bridge(@NotNull IoFlow flow, @NotNull I internals,
                            @NotNull P params);

            }

            /**
             * An {@code IoLink} without parameters.
             *
             * @param <I> the I/O state's internal type.
             * @see WithParams
             */
            @FunctionalInterface
            public interface WithoutParams<I> {

                /**
                 * Invoked by {@link IoAdapter} as necessary.
                 *
                 * @param flow      the current flow.
                 * @param internals the I/O state internals.
                 */
                void bridge(@NotNull IoFlow flow, @NotNull I internals);

            }

            private WithFlow() {
                /* prevent instantiation */
            }

        }

        /**
         * An {@code IoLink} which ignores the current {@link IoFlow} of
         * an {@link IoState}.
         *
         * @see WithFlow
         */
        protected static final class WithoutFlow {

            /**
             * An {@code IoLink} with parameters.
             *
             * @param <I> the I/O state's internal type.
             * @param <P> the I/O link's parameter type.
             * @see WithoutParams
             */
            @FunctionalInterface
            public interface WithParams<I, P> {

                /**
                 * Invoked by {@link IoAdapter} as necessary.
                 *
                 * @param internals the I/O state internals.
                 * @param params    the link parameters.
                 */
                void bridge(@NotNull I internals, @NotNull P params);

            }

            /**
             * An {@code IoLink} without parameters.
             *
             * @param <I> the I/O state's internal type.
             * @see WithParams
             */
            @FunctionalInterface
            public interface WithoutParams<I> {

                /**
                 * Invoked by {@link IoAdapter} as necessary.
                 *
                 * @param internals the I/O state internals.
                 */
                void bridge(@NotNull I internals);

            }

            private WithoutFlow() {
                /* prevent instantiation */
            }

        }

        private IoLink() {
            /* prevent instantiation */
        }

    }

    private static class IoMapping<I, P> {

        @NotNull P params;
        @Nullable I internals;
        @Nullable IoLink.WithFlow.WithParams<I, P> link;

        private IoMapping(@NotNull P params) {
            this.params = params;
        }

        @SuppressWarnings("ConstantConditions")
        private void crossBridge(@NotNull IoFlow flow) {
            link.bridge(flow, internals, params);
        }

    }

    private final Map<IoFeature<?>, IoMapping<?, ?>> mappings;

    public IoAdapter() {
        this.mappings = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    <F extends IoFeature<S>, S extends IoState<I>, I, P>
    void updateMappingCache(@NotNull F feature) {
        IoMapping<I, P> mapping = (IoMapping<I, P>) mappings.get(feature);
        if (mapping == null) {
            return; /* no cache to update */
        }

        /* TODO */
    }

    private void requireFlow(@NotNull IoFeature<?> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        if (feature.getFlow() == IoFlow.TWO_WAY) {
            String msg = "two-way feature must account for flow";
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Returns if an {@link IoFeature} is linked to an adapter method.
     *
     * @param feature the I/O feature to check.
     * @return {@code true} if {@code feature} is linked to an adapter
     * method, {@code false} otherwise.
     * @see #linkFeature(IoFeature, Object, IoLink.WithFlow.WithParams)
     */
    protected boolean isLinked(@NotNull IoFeature<?> feature) {
        return mappings.containsKey(feature);
    }

    /**
     * Links an {@link IoFeature} to an adapter method.
     * <p>
     * The result of {@code feature.getFlow()} shall determine when
     * {@code link} is invoked. This occurs when the device is queried and/or
     * updated. Take note that features with a dormant flow are not linkable,
     * as {@code link} would never be invoked.
     *
     * @param feature the I/O feature to link.
     * @param params  the parameters for the link. Depending on the feature
     *                and adapter, this could be something like a button ID,
     *                an {@code enum} member, or some other value.
     * @param link    the adapter method to link to.
     * @param <F>     the I/O feature type.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @param <P>     the parameters type.
     * @throws NullPointerException     if {@code feature}, {@code params},
     *                                  or {@code link} are {@code null}.
     * @throws IllegalArgumentException {@code feature.getFlow()} returns
     *                                  {@link IoFlow#DORMANT}.
     * @see #isLinked(IoFeature)
     * @see LinkShorthand
     * @see ForFeature
     */
    protected final <F extends IoFeature<S>, S extends IoState<I>, I, P>
    void linkFeature(@NotNull F feature, @NotNull P params,
                     @NotNull IoLink.WithFlow.WithParams<I, P> link) {
        Objects.requireNonNull(feature, "feature cannot be null");
        Objects.requireNonNull(params, "params cannot be null");
        Objects.requireNonNull(link, "link cannot be null");

        /*
         * It would not make sense to link an adapter function to a dormant
         * state, as they are never bridged. Assume this was a mistake by the
         * caller and throw an exception.
         */
        if (feature.getFlow() == IoFlow.DORMANT) {
            String msg = ""; // TODO
            throw new IllegalArgumentException(msg);
        }

        mappings.put(feature, new IoMapping<>(params));
        this.updateMappingCache(feature);
    }

    /**
     * Links an {@link IoFeature} to an adapter method, with the parameter
     * being the feature itself.
     * <p>
     * The result of {@code feature.getFlow()} shall determine when
     * {@code link} is invoked. This occurs when the device is queried and/or
     * updated. Take note that features with a dormant flow are not linkable,
     * as {@code link} would never be invoked.
     *
     * @param feature the I/O feature to link.
     * @param link    the adapter method to link to.
     * @param <F>     the I/O feature type.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @throws NullPointerException     if {@code feature} or {@code link}
     *                                  are {@code null}.
     * @throws IllegalArgumentException {@code feature.getFlow()} returns
     *                                  {@link IoFlow#DORMANT}.
     * @see #isLinked(IoFeature)
     * @see LinkShorthand
     * @see ForFeature
     */
    @IoApi.Shorthand
    protected final <F extends IoFeature<S>, S extends IoState<I>, I>
    void linkFeature(@NotNull F feature,
                     @NotNull IoLink.WithFlow.WithParams<I, F> link) {
        this.linkFeature(feature, feature, link);
    }

    /**
     * Links an {@link IoFeature} to an adapter method with no parameters.
     * <p>
     * The result of {@code feature.getFlow()} shall determine when
     * {@code link} is invoked. This occurs when the device is queried and/or
     * updated. Take note that features with a dormant flow are not linkable,
     * as {@code link} would never be invoked.
     *
     * @param feature the I/O feature to link.
     * @param link    the adapter method to link to.
     * @param <F>     the I/O feature type.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @throws NullPointerException     if {@code feature} or {@code link}
     *                                  are {@code null}.
     * @throws IllegalArgumentException {@code feature.getFlow()} returns
     *                                  {@link IoFlow#DORMANT}.
     * @see #isLinked(IoFeature)
     * @see LinkShorthand
     * @see ForFeature
     */
    @IoApi.Shorthand
    protected final <F extends IoFeature<S>, S extends IoState<I>, I>
    void linkFeature(@NotNull F feature,
                     @NotNull IoLink.WithFlow.WithoutParams<I> link) {
        this.linkFeature(feature, IoLink.EMPTY_PARAMS,
                (f, i, p) -> link.bridge(f, i));
    }

    /**
     * Links an {@link IoFeature} to an adapter method.
     * <p>
     * The result of {@code feature.getFlow()} shall determine when
     * {@code link} is invoked. This occurs when the device is queried and/or
     * updated. Take note that features with a dormant flow are not linkable,
     * as {@code link} would never be invoked. Features with a two-way flow
     * are not permitted either, as {@code link} does not accept a parameter
     * for the current flow on invocation.
     *
     * @param feature the I/O feature to link.
     * @param params  the parameters for the link. Depending on the feature
     *                and adapter, this could be something like a button ID,
     *                an {@code enum} member, or some other value.
     * @param link    the adapter method to link to.
     * @param <F>     the I/O feature type.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @param <P>     the parameters type.
     * @throws NullPointerException     if {@code feature}, {@code params},
     *                                  or {@code link} are {@code null}.
     * @throws IllegalArgumentException {@code feature.getFlow()} returns
     *                                  {@link IoFlow#DORMANT} or
     *                                  {@link IoFlow#TWO_WAY}.
     * @see #isLinked(IoFeature)
     * @see LinkShorthand
     * @see ForFeature
     */
    @IoApi.Shorthand
    protected final <F extends IoFeature<S>, S extends IoState<I>, I, P>
    void linkFeature(@NotNull F feature, @NotNull P params,
                     @NotNull IoLink.WithoutFlow.WithParams<I, P> link) {
        this.requireFlow(feature);
        this.linkFeature(feature, params,
                (f, i, p) -> link.bridge(i, p));
    }

    /**
     * Links an {@link IoFeature} to an adapter method, with the parameter
     * being the feature itself.
     * <p>
     * The result of {@code feature.getFlow()} shall determine when
     * {@code link} is invoked. This occurs when the device is queried and/or
     * updated. Take note that features with a dormant flow are not linkable,
     * as {@code link} would never be invoked. Features with a two-way flow
     * are not permitted either, as {@code link} does not accept a parameter
     * for the current flow on invocation.
     *
     * @param feature the I/O feature to link.
     * @param link    the adapter method to link to.
     * @param <F>     the I/O feature type.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @throws NullPointerException     if {@code feature} or {@code link}
     *                                  are {@code null}.
     * @throws IllegalArgumentException {@code feature.getFlow()} returns
     *                                  {@link IoFlow#DORMANT}.
     * @see #isLinked(IoFeature)
     * @see LinkShorthand
     * @see ForFeature
     */
    @IoApi.Shorthand
    protected final <F extends IoFeature<S>, S extends IoState<I>, I>
    void linkFeature(@NotNull F feature,
                     @NotNull IoLink.WithoutFlow.WithParams<I, F> link) {
        this.linkFeature(feature, feature, link);
    }

    /**
     * Links an {@link IoFeature} to an adapter method with no parameters.
     * <p>
     * The result of {@code feature.getFlow()} shall determine when
     * {@code link} is invoked. This occurs when the device is queried and/or
     * updated. Take note that features with a dormant flow are not linkable,
     * as {@code link} would never be invoked. Features with a two-way flow
     * are not permitted either, as {@code link} does not accept a parameter
     * for the current flow on invocation.
     *
     * @param feature the I/O feature to link.
     * @param link    the adapter method to link to.
     * @param <F>     the I/O feature type.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @throws NullPointerException     if {@code feature} or {@code link}
     *                                  are {@code null}.
     * @throws IllegalArgumentException {@code feature.getFlow()} returns
     *                                  {@link IoFlow#DORMANT} or
     *                                  {@link IoFlow#TWO_WAY}.
     * @see #isLinked(IoFeature)
     * @see LinkShorthand
     * @see ForFeature
     */
    @IoApi.Shorthand
    protected final <F extends IoFeature<S>, S extends IoState<I>, I>
    void linkFeature(@NotNull F feature,
                     @NotNull IoLink.WithoutFlow.WithoutParams<I> link) {
        this.requireFlow(feature);
        this.linkFeature(feature, IoLink.EMPTY_PARAMS,
                (f, i, p) -> link.bridge(i));
    }

    /**
     * Unlinks an {@link IoFeature} from its adapter method.
     * <p>
     * Features that were previously linked shall have their state reset
     * to their initial value via {@link IoState#reset()}.
     *
     * @param feature the feature to unlink.
     * @throws NullPointerException if {@code feature} is {@code null}.
     */
    /* template is needless here, but compiler fusses if it's absent. */
    protected final <F extends IoFeature<S>, S extends IoState<I>, I>
    void unlinkFeature(@NotNull F feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        mappings.remove(feature);
        this.updateMappingCache(feature);
    }

}
