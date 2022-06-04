package io.ketill.pressable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface, when implemented by an I/O device, provides support
 * for pressable feature configuration. If not implemented, the default
 * configuration will be used.
 *
 * @see PressableIoFeatureObserver
 * @see PressableIoFeatureConfig#DEFAULT
 */
public interface PressableIoFeatureSupport {

    /**
     * Returns the configuration governing pressable features.
     * <p>
     * This configuration is used to determine if a feature is being held
     * down, how quickly they should be virtually pressed, etc.
     *
     * @return the configuration governing pressable features.
     */
    @NotNull PressableIoFeatureConfigView getPressableConfig();

    /**
     * Sets the configuration governing pressable features. These are used
     * to determine if a feature is being held down, how quickly they should
     * be virtually pressed, etc.
     *
     * @param config the configuration to use. A value of {@code null} is
     *               permitted, and results in the default configuration
     *               as specified by {@link PressableIoFeatureConfig#DEFAULT}
     *               being used instead.
     * @see PressableIoFeatureConfig#valueOf(PressableIoFeatureConfigView)
     */
    void usePressableConfig(@Nullable PressableIoFeatureConfigView config);

}
