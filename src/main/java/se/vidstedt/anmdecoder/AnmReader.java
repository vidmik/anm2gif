package se.vidstedt.anmdecoder;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.IntStream;

class AnmReader {
    private static final int MAGIC = 0x2046504c; // "LPF "
    private static final int MAGIC_CONTENT_TYPE = 0x4d494e41; // "ANIM"
    private static final int MAX_PAGES = 256;

    Animation decode(File file) throws IOException {
        byte[] data = Files.readAllBytes(file.toPath());
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        AnmHeader header = new AnmHeader();

        header.id = bb.getInt();
        if (header.id != MAGIC) {
            throw new IllegalArgumentException();
        }
        header.maxLps = bb.getShort();
        header.nLps = bb.getShort();
        header.nRecords = bb.getInt();
        header.maxRecsPerLps = bb.getShort();
        header.lpfTableOffset = bb.getShort();
        header.contentType = bb.getInt();
        if (header.contentType != MAGIC_CONTENT_TYPE) {
            throw new IllegalArgumentException();
        }
        header.width = bb.getShort();
        header.height = bb.getShort();
        header.variant = bb.get();
        if (header.variant != 0) {
            throw new IllegalArgumentException();
        }
        header.version = bb.get();
        header.hasLastDelta = bb.get();
        header.lastDeltaValid = bb.get();
        header.pixelType = bb.get();
        if (header.pixelType != 0) {
            throw new IllegalArgumentException();
        }
        header.compressionType = bb.get();
        if (header.compressionType != 1) {
            throw new IllegalArgumentException();
        }
        header.otherRecsPerFrm = bb.get();
        if (header.otherRecsPerFrm != 0) {
            throw new IllegalArgumentException();
        }
        header.bitmapType = bb.get();
        if (header.bitmapType != 1) {
            throw new IllegalArgumentException();
        }
        bb.get(header.recordType);
        header.nFrames = bb.getInt();
        header.framesPerSecond = bb.getShort();
        bb.get(header.pad);
        header.cycles = IntStream.range(0, 16).mapToObj(i -> readRange(bb)).toArray(Range[]::new);

        if (bb.position() != 256) {
            throw new IllegalArgumentException("bb.position: " + bb.position());
        }

        int[] paletteEntries = new int[256];
        for (int i = 0; i < 256; i++) {
            paletteEntries[i] = bb.getInt();
        }
        Palette palette = new Palette(paletteEntries);

        if (bb.position() != 1280) {
            throw new IllegalArgumentException();
        }

        LargePageHeader[] largePagesHeaders = IntStream.range(0, 256).mapToObj(i -> readLargePageHeader(bb)).toArray(LargePageHeader[]::new);

        if (bb.position() != 1280 + 256 * 6) {
            throw new IllegalArgumentException();
        }

        ArrayList<LargePage> lps = new ArrayList<>();
        for (int i = 0; i < largePagesHeaders.length; i++) {
            if (largePagesHeaders[i].nRecords != 0) {
                bb.position(header.lpfTableOffset + MAX_PAGES * 6 + (i << 16));
                lps.add(readLargePage(bb, largePagesHeaders[i]));
            }
        }

        Record[] records = new Record[header.nRecords];
        for (LargePage lp : lps) {
            int base = lp.header.baseRecord;
            System.arraycopy(lp.records, 0, records, base, lp.header.nRecords);
        }

        return new Animation(header, palette, records);
    }

    private LargePageHeader readLargePageHeader(ByteBuffer bb) {
        int baseRecord = Short.toUnsignedInt(bb.getShort());
        int nRecords = Short.toUnsignedInt(bb.getShort());
        int nBytes = Short.toUnsignedInt(bb.getShort());

        return new LargePageHeader(baseRecord, nRecords, nBytes);
    }

    private LargePage readLargePage(ByteBuffer bb, LargePageHeader headerCopy) {
        int startPos = bb.position();

        LargePageHeader header = readLargePageHeader(bb);

        if (header.nRecords != headerCopy.nRecords) {
            throw new IllegalArgumentException();
        }
        if (header.baseRecord != headerCopy.baseRecord) {
            throw new IllegalArgumentException();
        }
        if (header.nBytes != headerCopy.nBytes) {
            throw new IllegalArgumentException();
        }

        short bytesContinued = bb.getShort();
        if (bytesContinued != 0) {
            throw new IllegalArgumentException();
        }

        int[] recordSizes = new int[header.nRecords];
        Record[] records = new Record[header.nRecords];
        for (int i = 0; i < header.nRecords; i++) {
            recordSizes[i] = Short.toUnsignedInt(bb.getShort());
        }

        for (int i = 0; i < header.nRecords; i++) {
            if (recordSizes[i] != 0) {
                records[i] = readRecord(bb, recordSizes[i]);
                if (records[i].data[0] != 66) {
                    throw new IllegalArgumentException();
                }
                if (records[i].data[1] != 0) {
                    throw new IllegalArgumentException();
                }
            }
        }

        int newPos = Math.min(startPos + 64 * 1024, bb.limit());
        bb.position(newPos);

        return new LargePage(header, bytesContinued, records, startPos);
    }

    private Range readRange(ByteBuffer bb) {
        short count = bb.getShort();
        short rate = bb.getShort();
        short flags = bb.getShort();
        byte low = bb.get();
        byte high = bb.get();

        return new Range(count, rate, flags, low, high);
    }

    private Record readRecord(ByteBuffer bb, int nBytes) {
        byte[] data = new byte[nBytes];
        bb.get(data);
        return new Record(data, bb.position());
    }
}
