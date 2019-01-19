package se.vidstedt.anm2gif;

class LargePage {
    private final LargePageHeader header;
    private final short bytesContinued;
    private final Record[] records;
    private final int fileOffset;

    LargePage(LargePageHeader header, short bytesContinued, Record[] records, int fileOffset) {
        this.header = header;
        this.bytesContinued = bytesContinued;
        this.records = records;
        this.fileOffset = fileOffset;
    }

    public LargePageHeader getHeader() {
        return header;
    }

    public Record[] getRecords() {
        return records;
    }

    public String toString() {
        return "{ header: + " + header + ", bytesContinued: " + bytesContinued + " fileOffset: " + fileOffset + " }";
    }
}
