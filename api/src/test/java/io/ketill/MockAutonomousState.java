package io.ketill;

import org.jetbrains.annotations.NotNull;

class MockAutonomousState implements AutonomousState {

    boolean updatedState;

    @Override
    public void update() {
        this.updatedState = true;
    }

}
