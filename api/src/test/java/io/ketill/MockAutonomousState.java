package io.ketill;

class MockAutonomousState implements AutonomousState {

    boolean updatedState;

    @Override
    public void update() {
        this.updatedState = true;
    }

}
