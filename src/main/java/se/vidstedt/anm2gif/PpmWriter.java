package se.vidstedt.anm2gif;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

class PpmWriter {
    private final int width, height;
    private final File file;
    private final byte[] data;

    public PpmWriter(int width, int height, File file, byte[] data) {
        if (data.length != width * height * 3) {
            throw new IllegalArgumentException();
        }

        this.width = width;
        this.height = height;
        this.file = file;
        this.data = data;
    }

    public void write() throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(String.format("P6\n%d %d\n255\n", width, height).getBytes(UTF_8));
            fos.write(data);
        }
    }
}
