package se.vidstedt.anmdecoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;

class RunSkipDump {
    public void runSkipDump(ByteBuffer src, ByteBuffer dst) {
        // skip the first two bytes (why?)
        src.get();
        src.get();

        while (src.position() < src.limit()) {
            byte b = src.get();
            if (b > 0) {
                // dump
                doDump(src, dst, b);
            } else if (b == 0) {
                // run
                int len = Byte.toUnsignedInt(src.get());
                byte pixel = src.get();
                doRun(dst, pixel, len);
            } else {
                b &= 0x7f;
                if (b != 0) {
                    // short skip
                    doSkip(dst, b);
                } else {
                    // longOp
                    short word = src.getShort();
                    if (word <= 0) {
                        if (word == 0) {
                            break;
                        }
                        word &= 0x7fff;
                        if (word >= 0x4000) {
                            // long run
                            word -= 0x4000;
                            byte pixel = src.get();
                            doRun(dst, pixel, word);
                        } else {
                            // long dump
                            doDump(src, dst, word);
                        }
                    } else {
                        // long skip
                        doSkip(dst, word);
                    }
                }
            }
        }
    }

    private void doDump(ByteBuffer src, ByteBuffer dst, int n) {
        for (int i = 0; i < n; i++) {
            dst.put(src.get());
        }
    }

    private void doRun(ByteBuffer dst, byte pixel, int n) {
        for (int i = 0; i < n; i++) {
            dst.put(pixel);
        }
    }

    private void doSkip(ByteBuffer dst, int n) {
        dst.position(dst.position() + n);
    }
}
