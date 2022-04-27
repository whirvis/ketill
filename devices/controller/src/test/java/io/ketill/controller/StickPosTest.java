package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class StickPosTest {

    private StickPosZ internal;
    private StickPos container;

    @BeforeEach
    void setup() {
        this.internal = spy(new StickPosZ());
        this.container = new StickPos(internal);
    }

    @Test
    void __init__() {
        assertThrows(NullPointerException.class,
                () -> new StickPos(null));
    }

}
