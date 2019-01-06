package se.vidstedt.anmdecoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

class PpmWriter {
    private final int width, height;
    private final File file;

    public PpmWriter(int width, int height, File file) {
        this.width = width;
        this.height = height;
        this.file = file;
    }

    public void write(byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(String.format("P6\n%d %d\n255\n", width, height).getBytes(UTF_8));
            fos.write(data);
        }
    }
}
