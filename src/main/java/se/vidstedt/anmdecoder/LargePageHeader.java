package se.vidstedt.anmdecoder;

class LargePageHeader {
    final int baseRecord;
    final int nRecords;
    final int nBytes;

    public LargePageHeader(int baseRecord, int nRecords, int nBytes) {
        this.baseRecord = baseRecord;
        this.nRecords = nRecords;
        this.nBytes = nBytes;
    }

    public String toString() {
        return String.format("{ baseRecord: 0x%x, nRecords: 0x%x, nBytes: 0x%x -> endRecord: 0x%x }", baseRecord, nRecords, nBytes, baseRecord + nRecords);
    }
}
