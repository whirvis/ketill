package io.ketill.glfw;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

class MockDeviceGuids extends DeviceGuids {

    int getGuidsImplCallCount;
    String lastRequestedOs;
    String currentSystemId;
    String[] currentGuids;

    MockDeviceGuids() {
        super();
    }

    MockDeviceGuids(boolean useDefaultSystems) {
        super(useDefaultSystems);
    }

    @Override
    protected Collection<String> getGuidsImpl(@NotNull String systemId) {
        this.getGuidsImplCallCount++;
        this.lastRequestedOs = systemId;
        if (currentSystemId == null || currentGuids == null) {
            return null;
        } else if (!currentSystemId.equals(systemId)) {
            return null;
        }
        return Arrays.asList(currentGuids);
    }

}
