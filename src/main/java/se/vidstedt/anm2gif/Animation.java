package se.vidstedt.anm2gif;

class Animation {
    private final AnmHeader header;
    private final Palette palette;
    private final Record[] records;

    public Animation(AnmHeader header, Palette palette, Record[] records) {
        this.header = header;
        this.palette = palette;
        this.records = records;
    }

    public AnmHeader getHeader() {
        return header;
    }

    public Palette getPalette() {
        return palette;
    }

    public Record[] getRecords() {
        return records;
    }

    public Record getRecord(int index) {
        return records[index];
    }
}
