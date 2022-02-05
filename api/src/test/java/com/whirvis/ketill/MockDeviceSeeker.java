package com.whirvis.ketill;

class MockDeviceSeeker extends DeviceSeeker<MockInputDevice> {

    boolean errorOnSeek;
    private boolean seeked;

    boolean hasSeeked() {
        return this.seeked;
    }

    @Override
    protected void seekImpl() throws Exception {
        if (errorOnSeek) {
            throw new InputException();
        }
        this.seeked = true;
    }

}
