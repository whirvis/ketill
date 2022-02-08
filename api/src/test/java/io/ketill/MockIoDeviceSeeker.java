package io.ketill;

class MockIoDeviceSeeker extends IoDeviceSeeker<MockIoDevice> {

    boolean errorOnSeek;
    private boolean seeked;

    boolean hasSeeked() {
        return this.seeked;
    }

    @Override
    protected void seekImpl() throws Exception {
        if (errorOnSeek) {
            throw new KetillException();
        }
        this.seeked = true;
    }

}
