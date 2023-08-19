package io.ketill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;
import java.util.*;

/**
 * Maps data from a source (such as GLFW or X-input) to the features
 * of an {@link IoDevice}. This allows a single device to be used with
 * different implementations.
 *
 * @param <D> the I/O device type.
 * @see ForFeature
 * @see LinkMethod
 * @see ParamType
 * @see IoLink
 */
@SuppressWarnings("unused")
public abstract class IoHandle<D extends IoDevice> {

    /**
     * When present, signals that a method is an adapter for one or more
     * features. It is <b>required</b> the access level of these methods
     * be {@code private}.
     * <p>
     * The following is an example use of this annotation.
     * <pre>
     * TODO
     * </pre>
     *
     * @see LinkMethod
     */
    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface ForFeature {
        /* this annotation has no attributes */
    }

    /**
     * When present, signals that a method is a shorthand for linking
     * a feature to an adapter method. It is <b>required</b> the access
     * level of these methods be {@code protected}.
     * <p>
     * The following is an example use of this annotation.
     * <pre>
     * TODO
     * </pre>
     *
     * @see ForFeature
     * @see ParamType
     */
    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface LinkMethod {
        /* this annotation has no attributes */
    }

    /**
     * When present, indicates a type is used solely as a parameter for linking
     * an {@link IoFeature} to an adapter. These are intended for features that
     * require more than a single field to be linked. An example of this would
     * be an analog stick.
     * <p>
     * The following is an example use of this annotation.
     * <pre>
     * &#64;IoAdapter.ParamType
     * class IoStickMapping {
     *
     *     final int xAxis;
     *     final int yAxis;
     *
     *     StickMapping(int xAxis, int yAxis) {
     *         this.xAxis = xAxis;
     *         this.yAxis = yAxis;
     *     }
     *
     * }
     * </pre>
     *
     * @see LinkMethod
     */
    @Documented
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ParamType {
        /* this annotation has no attributes */
    }

    /**
     * An adapter method for an {@link IoFeature}.
     *
     * @see #linkFeature(IoFeature, Object, WithFlow.WithParams)
     */
    protected static final class IoLink {

        static final Object NO_PARAMS = new Object();

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
                 * Invoked by {@link IoHandle} as necessary.
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
                 * Invoked by {@link IoHandle} as necessary.
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
                 * Invoked by {@link IoHandle} as necessary.
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
                 * Invoked by {@link IoHandle} as necessary.
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
        @NotNull IoLink.WithFlow.WithParams<I, P> link;

        @Nullable IoState<I> state;

        private IoMapping(@NotNull P params,
                          @NotNull IoLink.WithFlow.WithParams<I, P> link) {
            this.params = params;
            this.link = link;
        }

        /*
         * The warning IntelliJ gives about internals possibly being null is
         * completely valid. However, all instances of IoMapping wrap around
         * different IoLink types into an instance of WithParams. This is to
         * prevent a branch which would slow down execution.
         *
         * If internals is null, it is assumed the current link is a wrapper
         * for an IoLink which does not take any internals.
         */
        @SuppressWarnings("ConstantConditions")
        private void crossBridge(@NotNull IoFlow flow) {
            state.preprocess(flow);
            link.bridge(flow, internals, params);
            state.postprocess(flow);
        }

    }

    private static class IoMappingCache {

        private final IoFlow flow;
        private final Set<IoMapping<?, ?>> mappings;

        private IoMappingCache(IoFlow flow) {
            this.flow = flow;
            this.mappings = new HashSet<>();
        }

        public void add(IoMapping<?, ?> mapping) {
            mappings.add(mapping);
        }

        public void remove(IoMapping<?, ?> mapping) {
            mappings.remove(mapping);
        }

        public void bridge() {
            for (IoMapping<?, ?> mapping : mappings) {
                mapping.crossBridge(flow);
            }
        }

    }

    private final @NotNull Map<IoFeature<?>, IoMapping<?, ?>> mappings;
    private final @NotNull IoMappingCache inMappings, outMappings;

    private @Nullable D device;
    private boolean linked;

    IoHandle() {
        this.mappings = new HashMap<>();
        this.inMappings = new IoMappingCache(IoFlow.IN);
        this.outMappings = new IoMappingCache(IoFlow.OUT);
    }

    public final D getDevice() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @IoApi.Friends(IoDevice.class)
    <F extends IoFeature<? extends S>, S extends IoState<? extends I>, I, P>
    void updateMappingCache(@NotNull F feature) {
        if (device == null) {
            return;
        }

        IoMapping<I, P> mapping = (IoMapping<I, P>) mappings.get(feature);
        if (mapping == null) {
            return; /* feature is not mapped */
        }

        // mapping.internals = device.getInternals(feature);
        // mapping.state = (IoState<I>) device.getState(feature);

        if (mapping.internals == null) {
            inMappings.remove(mapping);
            outMappings.remove(mapping);
        } else {
            IoFlow flow = feature.getFlow();
            if (flow.flowsInward()) {
                inMappings.add(mapping);
            }
            if (flow.flowsOutward()) {
                outMappings.add(mapping);
            }
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
     * The result of {@code feature.getFlow()} determines when {@code link}
     * is invoked. This occurs when the device is queried and/or updated.
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
     * @throws NullPointerException if {@code feature}, {@code params},
     *                              or {@code link} are {@code null}.
     * @see #isLinked(IoFeature)
     * @see LinkMethod
     * @see ForFeature
     */
    protected final <F extends IoFeature<? extends S>, S extends IoState<? extends I>, I, P>
    void linkFeature(@NotNull F feature, @NotNull P params,
                     @NotNull IoLink.WithFlow.WithParams<I, P> link) {
        Objects.requireNonNull(feature, "feature cannot be null");
        Objects.requireNonNull(params, "params cannot be null");
        Objects.requireNonNull(link, "link cannot be null");

        mappings.put(feature, new IoMapping<>(params, link));
        this.updateMappingCache(feature);
    }

    /**
     * Links an {@link IoFeature} to an adapter method, with the parameter
     * being the feature itself.
     * <p>
     * The result of {@code feature.getFlow()} determines when {@code link}
     * is invoked. This occurs when the device is queried and/or updated.
     *
     * @param feature the I/O feature to link.
     * @param link    the adapter method to link to.
     * @param <F>     the I/O feature type.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @throws NullPointerException if {@code feature} or {@code link}
     *                              are {@code null}.
     * @see #isLinked(IoFeature)
     * @see LinkMethod
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
     * The result of {@code feature.getFlow()} determines when {@code link}
     * is invoked. This occurs when the device is queried and/or updated.
     *
     * @param feature the I/O feature to link.
     * @param link    the adapter method to link to.
     * @param <F>     the I/O feature type.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @throws NullPointerException if {@code feature} or {@code link}
     *                              are {@code null}.
     * @see #isLinked(IoFeature)
     * @see LinkMethod
     * @see ForFeature
     */
    @IoApi.Shorthand
    protected final <F extends IoFeature<S>, S extends IoState<I>, I>
    void linkFeature(@NotNull F feature,
                     @NotNull IoLink.WithFlow.WithoutParams<I> link) {
        this.linkFeature(feature, IoLink.NO_PARAMS,
                (f, i, p) -> link.bridge(f, i));
    }

    /**
     * Ensures that an {@link IoFeature} flows in a single direction.
     *
     * @param feature the feature to validate.
     * @throws NullPointerException     if {@code feature} is {@code null}.
     * @throws IllegalArgumentException if {@code feature.getFlow()} returns
     *                                  {@link IoFlow#TWO_WAY}.
     */
    private void requireOneWayFlow(@NotNull IoFeature<?> feature) {
        Objects.requireNonNull(feature, "feature cannot be null");
        if (feature.getFlow() == IoFlow.TWO_WAY) {
            String msg = "two-way feature must account for flow";
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Links an {@link IoFeature} to an adapter method.
     * <p>
     * The result of {@code feature.getFlow()} determines when {@code link}
     * is invoked. This occurs when the device is queried and/or updated.
     * Take note that features with a two-way flow are not permitted here,
     * as {@code link} does not accept a parameter for the current flow
     * on invocation.
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
     *                                  {@link IoFlow#TWO_WAY}.
     * @see #isLinked(IoFeature)
     * @see LinkMethod
     * @see ForFeature
     */
    @IoApi.Shorthand
    protected final <F extends IoFeature<S>, S extends IoState<I>, I, P>
    void linkFeature(@NotNull F feature, @NotNull P params,
                     @NotNull IoLink.WithoutFlow.WithParams<I, P> link) {
        this.requireOneWayFlow(feature);
        this.linkFeature(feature, params,
                (f, i, p) -> link.bridge(i, p));
    }

    /**
     * Links an {@link IoFeature} to an adapter method, with the parameter
     * being the feature itself.
     * <p>
     * The result of {@code feature.getFlow()} determines when {@code link}
     * is invoked. This occurs when the device is queried and/or updated.
     * Take note that features with a two-way flow are not permitted here,
     * as {@code link} does not accept a parameter for the current flow
     * on invocation.
     *
     * @param feature the I/O feature to link.
     * @param link    the adapter method to link to.
     * @param <F>     the I/O feature type.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @throws NullPointerException if {@code feature} or {@code link}
     *                              are {@code null}.
     * @see #isLinked(IoFeature)
     * @see LinkMethod
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
     * The result of {@code feature.getFlow()} determines when {@code link}
     * is invoked. This occurs when the device is queried and/or updated.
     *
     * @param feature the I/O feature to link.
     * @param link    the adapter method to link to.
     * @param <F>     the I/O feature type.
     * @param <S>     the I/O state type.
     * @param <I>     the internal data type.
     * @throws NullPointerException     if {@code feature} or {@code link}
     *                                  are {@code null}.
     * @throws IllegalArgumentException {@code feature.getFlow()} returns
     *                                  {@link IoFlow#TWO_WAY}.
     * @see #isLinked(IoFeature)
     * @see LinkMethod
     * @see ForFeature
     */
    @IoApi.Shorthand
    protected final <F extends IoFeature<S>, S extends IoState<I>, I>
    void linkFeature(@NotNull F feature,
                     @NotNull IoLink.WithoutFlow.WithoutParams<I> link) {
        this.requireOneWayFlow(feature);
        this.linkFeature(feature, IoLink.NO_PARAMS,
                (f, i, p) -> link.bridge(i));
    }

    /**
     * Unlinks an {@link IoFeature} from its adapter method.
     * <p>
     * Features that were previously linked shall have their state reset.
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

    /* TODO: documentations */
    public void linkDevice(@NotNull D device) {
        Objects.requireNonNull(device, "device cannot be null");
        if (linked) {
            throw new IllegalStateException("adapter already linked");
        }

        // device.adapter = this;

        try {
            // this.openDevice(device, mode);
            this.device = device;

            for (IoFeature<?> feature : mappings.keySet()) {
                this.updateMappingCache(feature);
            }

            this.linked = true;
        } catch (IoHandleException unwrapped) {
            throw unwrapped; /* do not wrap */
        } catch (Exception e) {
            throw new IoHandleException("error linking device", e);
        }
    }

    // isDeviceConnected()

}