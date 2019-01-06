package se.vidstedt.anmdecoder;

class LargePage {
    final LargePageHeader header;
    private final short bytesContinued;
    final Record[] records;
    private final int fileOffset;

    LargePage(LargePageHeader header, short bytesContinued, Record[] records, int fileOffset) {
        this.header = header;
        this.bytesContinued = bytesContinued;
        this.records = records;
        this.fileOffset = fileOffset;
    }

    public String toString() {
        return "{ header: + " + header + ", bytesContinued: " + bytesContinued + " fileOffset: " + fileOffset + " }";
    }
}
