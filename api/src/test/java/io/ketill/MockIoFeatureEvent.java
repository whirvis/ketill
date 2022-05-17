package io.ketill;

import org.jetbrains.annotations.NotNull;

class MockIoFeatureEvent extends IoFeatureEvent {

    MockIoFeatureEvent(@NotNull IoDevice device,
                       @NotNull IoFeature<?, ?> feature) {
        super(device, feature);
    }

}
