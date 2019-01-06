package se.vidstedt.anmdecoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Record {
    final byte[] data;
    private final int fileOffset;

    Record(byte[] data, int fileOffset) {
        this.data = data;
        this.fileOffset = fileOffset;
    }

    public void decode(byte[] pixels) {
        ByteBuffer dst = ByteBuffer.wrap(pixels).order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer src = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        src.get(); src.get();
        new RunSkipDump().runSkipDump(src, dst);
    }
}
