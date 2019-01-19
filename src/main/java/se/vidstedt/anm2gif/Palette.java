package se.vidstedt.anm2gif;

public class Palette {
    private final int[] palette;

    public Palette(int[] palette) {
        this.palette = palette;
    }

    public int getPalette(byte index) {
        return palette[Byte.toUnsignedInt(index)];
    }

    public byte[] getPaletteColorComponents(byte index) {
        int color = getPalette(index);
        return new byte[]{
                (byte)((color >> 16) & 0xff), // red
                (byte)((color >> 8) & 0xff),  // green
                (byte)(color & 0xff)          // blue
        };
    }
}
