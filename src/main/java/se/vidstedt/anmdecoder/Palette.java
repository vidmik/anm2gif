package se.vidstedt.anmdecoder;

public class Palette {
    private final int[] palette;

    public Palette(int[] palette) {
        this.palette = palette;
    }

    private int getPalette(int index) {
        return palette[index];
    }

    public int[] getPaletteColorComponents(int index) {
        int color = getPalette(index);
        return new int[]{
                (color >> 16) & 0xff, // red
                (color >> 8) & 0xff,  // green
                color & 0xff          // blue
        };
    }
}
