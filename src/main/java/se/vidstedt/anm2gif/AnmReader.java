package se.vidstedt.anm2gif;

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

    Animation read(File file) throws IOException {
        byte[] data = Files.readAllBytes(file.toPath());
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        AnmHeader header = readHeader(bb);
        if (bb.position() != 256) {
            throw new IllegalArgumentException("bb.position: " + bb.position());
        }

        Palette palette = readPalette(bb);
        if (bb.position() != 1280) {
            throw new IllegalArgumentException();
        }

        LargePageHeader[] largePagesHeaders = readLargePageHeaders(bb);
        if (bb.position() != 1280 + 256 * 6) {
            throw new IllegalArgumentException();
        }

        ArrayList<LargePage> lps = new ArrayList<>();
        for (int i = 0; i < largePagesHeaders.length; i++) {
            if (largePagesHeaders[i].getnRecords() != 0) {
                bb.position(header.getLpfTableOffset() + MAX_PAGES * 6 + (i << 16));
                lps.add(readLargePage(bb, largePagesHeaders[i]));
            }
        }

        Record[] records = new Record[header.getnRecords()];
        for (LargePage lp : lps) {
            int base = lp.getHeader().getBaseRecord();
            System.arraycopy(lp.getRecords(), 0, records, base, lp.getHeader().getnRecords());
        }

        return new Animation(header, palette, records);
    }

    private AnmHeader readHeader(ByteBuffer bb) {
        int id = bb.getInt();
        if (id != MAGIC) {
            throw new IllegalArgumentException();
        }
        short maxLps = bb.getShort();
        short nLps = bb.getShort();
        int nRecords = bb.getInt();
        short maxRecsPerLps = bb.getShort();
        short lpfTableOffset = bb.getShort();
        int contentType = bb.getInt();
        if (contentType != MAGIC_CONTENT_TYPE) {
            throw new IllegalArgumentException();
        }
        short width = bb.getShort();
        short height = bb.getShort();
        byte variant = bb.get();
        if (variant != 0) {
            throw new IllegalArgumentException();
        }
        byte version = bb.get();
        byte hasLastDelta = bb.get();
        byte lastDeltaValid = bb.get();
        byte pixelType = bb.get();
        if (pixelType != 0) {
            throw new IllegalArgumentException();
        }
        byte compressionType = bb.get();
        if (compressionType != 1) {
            throw new IllegalArgumentException();
        }
        byte otherRecsPerFrm = bb.get();
        if (otherRecsPerFrm != 0) {
            throw new IllegalArgumentException();
        }
        byte bitmapType = bb.get();
        if (bitmapType != 1) {
            throw new IllegalArgumentException();
        }
        byte[] recordType = new byte[32];
        bb.get(recordType);
        int nFrames = bb.getInt();
        short framesPerSecond = bb.getShort();
        byte[] pad = new byte[29 * 2];
        bb.get(pad);
        Range[] cycles = IntStream.range(0, 16).mapToObj(i -> readRange(bb)).toArray(Range[]::new);

        return new AnmHeader(id, maxLps, nLps, nRecords, maxRecsPerLps, lpfTableOffset, contentType,
                width, height, variant, version, hasLastDelta, lastDeltaValid, pixelType, compressionType,
                otherRecsPerFrm, bitmapType, recordType, nFrames, framesPerSecond, pad, cycles);
    }

    private Palette readPalette(ByteBuffer bb) {
        int[] paletteEntries = new int[256];
        for (int i = 0; i < 256; i++) {
            paletteEntries[i] = bb.getInt();
        }
        return new Palette(paletteEntries);
    }

    private LargePageHeader[] readLargePageHeaders(ByteBuffer bb) {
        return IntStream.range(0, 256).mapToObj(i -> readLargePageHeader(bb)).toArray(LargePageHeader[]::new);
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

        if (header.getnRecords() != headerCopy.getnRecords()) {
            throw new IllegalArgumentException();
        }
        if (header.getBaseRecord() != headerCopy.getBaseRecord()) {
            throw new IllegalArgumentException();
        }
        if (header.getnBytes() != headerCopy.getnBytes()) {
            throw new IllegalArgumentException();
        }

        short bytesContinued = bb.getShort();
        if (bytesContinued != 0) {
            throw new IllegalArgumentException();
        }

        int[] recordSizes = new int[header.getnRecords()];
        Record[] records = new Record[header.getnRecords()];
        for (int i = 0; i < header.getnRecords(); i++) {
            recordSizes[i] = Short.toUnsignedInt(bb.getShort());
        }

        for (int i = 0; i < header.getnRecords(); i++) {
            if (recordSizes[i] != 0) {
                records[i] = readRecord(bb, recordSizes[i]);
                if (records[i].getData()[0] != 66) {
                    throw new IllegalArgumentException();
                }
                if (records[i].getData()[1] != 0) {
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
