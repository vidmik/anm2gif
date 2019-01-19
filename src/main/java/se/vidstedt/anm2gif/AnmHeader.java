package se.vidstedt.anm2gif;

class AnmHeader {
    private final int id;
    private final short maxLps;
    private final short nLps;
    private final int nRecords;
    private final short maxRecsPerLps;
    private final short lpfTableOffset;
    private final int contentType;
    private final short width;
    private final short height;
    private final byte variant;
    private final byte version;
    private final byte hasLastDelta;
    private final byte lastDeltaValid;
    private final byte pixelType;
    private final byte compressionType;
    private final byte otherRecsPerFrm;
    private final byte bitmapType;
    private final byte[] recordType;
    private final int nFrames;
    private final short framesPerSecond;
    private final byte[] pad;
    private final Range[] cycles;

    public AnmHeader(int id, short maxLps, short nLps, int nRecords, short maxRecsPerLps, short lpfTableOffset, int contentType, short width, short height, byte variant, byte version, byte hasLastDelta, byte lastDeltaValid, byte pixelType, byte compressionType, byte otherRecsPerFrm, byte bitmapType, byte[] recordType, int nFrames, short framesPerSecond, byte[] pad, Range[] cycles) {
        this.id = id;
        this.maxLps = maxLps;
        this.nLps = nLps;
        this.nRecords = nRecords;
        this.maxRecsPerLps = maxRecsPerLps;
        this.lpfTableOffset = lpfTableOffset;
        this.contentType = contentType;
        this.width = width;
        this.height = height;
        this.variant = variant;
        this.version = version;
        this.hasLastDelta = hasLastDelta;
        this.lastDeltaValid = lastDeltaValid;
        this.pixelType = pixelType;
        this.compressionType = compressionType;
        this.otherRecsPerFrm = otherRecsPerFrm;
        this.bitmapType = bitmapType;
        this.recordType = recordType;
        this.nFrames = nFrames;
        this.framesPerSecond = framesPerSecond;
        this.pad = pad;
        this.cycles = cycles;
    }

    public int getnRecords() {
        return nRecords;
    }

    public short getLpfTableOffset() {
        return lpfTableOffset;
    }

    public int getWidth() {
        return Short.toUnsignedInt(width);
    }

    public int getHeight() {
        return Short.toUnsignedInt(height);
    }

    public String toString() {
        return "{\n"
                + "id: " + id + ", " +
                "maxLps: " + maxLps + ", " +
                "nLps: " + nLps + ", " +
                "nRecords: " + nRecords + ", " +
                "contentType: " + contentType + " " +
                "width: " + width + " " +
                "height: " + height + " " +
                "}";
    }
}
