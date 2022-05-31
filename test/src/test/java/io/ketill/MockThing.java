package io.ketill;

class MockThing {

    static class NoOverride {

    }

    static class ReturnsSuper {

        @Override
        public String toString() {
            return super.toString();
        }

    }

    static class ProperToString {

        @Override
        public String toString() {
            return "";
        }

    }

}
