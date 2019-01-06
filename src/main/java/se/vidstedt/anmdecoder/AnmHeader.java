package se.vidstedt.anmdecoder;

class AnmHeader {
    int id;
    short maxLps;
    short nLps;
    int nRecords;
    short maxRecsPerLps;
    short lpfTableOffset;
    int contentType;
    short width;
    short height;
    byte variant;
    byte version;
    byte hasLastDelta;
    byte lastDeltaValid;
    byte pixelType;
    byte compressionType;
    byte otherRecsPerFrm;
    byte bitmapType;
    final byte[] recordType = new byte[32];
    int nFrames;
    short framesPerSecond;
    final byte[] pad = new byte[29 * 2];
    Range[] cycles;

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
