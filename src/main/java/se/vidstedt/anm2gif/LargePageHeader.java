package se.vidstedt.anm2gif;

class LargePageHeader {
    private final int baseRecord;
    private final int nRecords;
    private final int nBytes;

    public LargePageHeader(int baseRecord, int nRecords, int nBytes) {
        this.baseRecord = baseRecord;
        this.nRecords = nRecords;
        this.nBytes = nBytes;
    }

    public int getBaseRecord() {
        return baseRecord;
    }

    public int getnRecords() {
        return nRecords;
    }

    public int getnBytes() {
        return nBytes;
    }

    public String toString() {
        return String.format("{ baseRecord: 0x%x, nRecords: 0x%x, nBytes: 0x%x -> endRecord: 0x%x }", baseRecord, nRecords, nBytes, baseRecord + nRecords);
    }
}
