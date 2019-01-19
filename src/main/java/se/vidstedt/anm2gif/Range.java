package se.vidstedt.anm2gif;

class Range {
    private final short count;
    private final short rate;
    private final short flags;
    private final byte low;
    private final byte high;

    Range(short count, short rate, short flags, byte low, byte high) {
        this.count = count;
        this.rate = rate;
        this.flags = flags;
        this.low = low;
        this.high = high;
    }

    public String toString() {
        return String.format("{ count: %d, rate: %d, flags: 0x%02x, low: %d, high: %d }", count, rate, flags, Byte.toUnsignedInt(low), Byte.toUnsignedInt(high));
    }
}
